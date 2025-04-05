package com.ecmmanage.controller;

import com.ecmmanage.model.User;
import com.ecmmanage.service.JwtService;
import com.ecmmanage.service.UserService;
import com.ecmmanage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.mockito.Mock;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController authController;
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        authController = new AuthController(authenticationManager, userService, jwtService, userRepository);

    }

    @Test
    void testLoginSuccess() {
        // ✅ Mock authentication success.
        String username = "user1";
        String password = "password";
        String token = "test-jwt-token";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        // ✅ Call login method.
        ResponseEntity<AuthController.AuthResponse> response = authController.login(new AuthController.LoginRequest(username, password));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(token, response.getBody().message());
    }

    @Test
    void testLoginFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        ResponseEntity<AuthController.AuthResponse> response = authController.login(new AuthController.LoginRequest("user1", "wrongpassword"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Invalid username or password", response.getBody().message());
    }

    @Test
    void testRegisterUser() {
        when(userService.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userService.registerUser(anyString(), anyString(), anyString())).thenReturn(new User());

        ResponseEntity<?> response = authController.registerUser(new AuthController.RegisterRequest("newUser", "password", "USER"));

        assertEquals(200, response.getStatusCode().value());
    }
}
