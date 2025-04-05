package com.ecmmanage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // ✅ Set properly base64-encoded secret key
        String rawSecret = "mySuperSecretKeyForTestingOnlyChangeItInProd12345";
        String encodedSecret = Base64.getEncoder().encodeToString(rawSecret.getBytes());
        jwtService.setSecretKey(encodedSecret);

        // ✅ Set expiration
        jwtService.setJwtExpirationMs(3600000); // 1 hour
    }

    @Test
    void testGenerateAndExtractToken() {
        UserDetails userDetails = new User(
            "testuser",
            "password",
            List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            )
        );

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        List<String> extractedRoles = jwtService.extractRoles(token);

        assertEquals("testuser", extractedUsername);
        assertTrue(extractedRoles.contains("ROLE_ADMIN"));
        assertTrue(extractedRoles.contains("ROLE_USER"));
    }
}
