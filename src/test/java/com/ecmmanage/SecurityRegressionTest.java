package com.ecmmanage;

import com.ecmmanage.model.Role;
import com.ecmmanage.model.User;
import com.ecmmanage.repository.UserRepository;
import com.ecmmanage.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityRegressionTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtService jwt;
    @Autowired ObjectMapper json;
    @Autowired jakarta.persistence.EntityManager entityManager;

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "admin", "ROLE_ADMIN", " Admin ", "MANAGER"})
    void publicRegistrationCannotChooseAnElevatedRole(String requestedRole) throws Exception {
        String username = "signup-" + UUID.randomUUID();
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("username", username,
                        "password", "test-password", "role", requestedRole))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
        User stored = users.findByUsername(username).orElseThrow();
        assertEquals(Role.USER, stored.getRole());
        assertTrue(passwords.matches("test-password", stored.getPassword()));
    }

    @Test
    void registrationWithoutRoleAndLoginReturnAUsableToken() throws Exception {
        String username = "login-" + UUID.randomUUID();
        String body = json.writeValueAsString(Map.of("username", username, "password", "test-password"));
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("USER"));
        String response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = json.readTree(response).get("message").asText();
        mvc.perform(get("/api/user/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void userReadAndUpdateResponsesNeverExposePasswordHashes() throws Exception {
        User admin = saveUser(Role.ADMIN);
        String token = jwt.generateToken(admin);
        mvc.perform(get("/api/user/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[*].password").isEmpty());
        mvc.perform(get("/api/user/by-username/" + admin.getUsername()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(put("/api/user/update/" + admin.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(
                        Map.of("username", admin.getUsername(), "password", "replacement-password"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.password").doesNotExist());
        assertTrue(passwords.matches("replacement-password", users.findById(admin.getId()).orElseThrow().getPassword()));
    }

    @Test
    void regularUserCannotListUpdateOrDeleteUsers() throws Exception {
        User user = saveUser(Role.USER);
        String token = jwt.generateToken(user);
        mvc.perform(get("/api/user/all").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/user/delete/" + user.getId()).header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        mvc.perform(put("/api/user/update/" + user.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{}")) .andExpect(status().isForbidden());
    }

    @Test
    void currentDatabaseRoleOverridesAnOldAdminToken() throws Exception {
        User user = saveUser(Role.ADMIN);
        String token = jwt.generateToken(user);
        user.setRole(Role.USER);
        users.saveAndFlush(user);
        mvc.perform(get("/api/user/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousAndMalformedTokensCannotAccessProtectedEndpoints() throws Exception {
        mvc.perform(get("/documents/all")).andExpect(status().isForbidden());
        mvc.perform(get("/documents/all").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void documentsAndWorkflowRunThroughTheAuthenticatedApi() throws Exception {
        String token = jwt.generateToken(saveUser(Role.USER));
        mvc.perform(post("/documents/upload").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Demo\",\"fileName\":\"demo.txt\",\"content\":\"Sample text\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber());
        mvc.perform(get("/documents/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Demo"));
        String body = mvc.perform(post("/workflows/start").param("workflowName", "Review demo")
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        entityManager.flush();
        entityManager.clear();
        mvc.perform(post("/workflows/auto-process/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.currentStep").value("Approval Step"));
        entityManager.flush();
        entityManager.clear();
        mvc.perform(post("/workflows/approve/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.currentStep").value("Final Step"));
        mvc.perform(post("/workflows/auto-process/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Completed"));
    }

    @Test
    void documentUploadRejectsMissingFilenameAndClientSuppliedId() throws Exception {
        String token = jwt.generateToken(saveUser(Role.USER));
        for (String body : java.util.List.of(
                "{\"title\":\"Demo\",\"content\":\"Text\"}",
                "{\"id\":42,\"title\":\"Demo\",\"content\":\"Text\",\"fileName\":\"demo.txt\"}")) {
            mvc.perform(post("/documents/upload").header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        }
    }

    private User saveUser(Role role) {
        return users.saveAndFlush(new User("test-" + UUID.randomUUID(), passwords.encode("test-password"), role));
    }
}
