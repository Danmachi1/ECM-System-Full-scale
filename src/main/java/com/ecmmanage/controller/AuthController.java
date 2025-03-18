// ✅ Declares the package for this controller.
package com.ecmmanage.controller;

// ✅ Import necessary Spring Security, JWT, and API components.
import com.ecmmanage.model.User;
import com.ecmmanage.service.JwtService;
import com.ecmmanage.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * ✅ Authentication Controller:
 * - Handles user **login & registration**.
 * - Uses **JWT authentication** to secure API access.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService; // ✅ Service for generating JWT tokens.

    /**
     * ✅ Constructor injection for dependencies.
     */
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * ✅ Login Endpoint:
     * - Authenticates user credentials.
     * - Generates and returns a JWT token upon success.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // ✅ Verify username & password with Spring Security's AuthenticationManager.
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // ✅ If authentication succeeds, set security context.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ✅ Generate JWT Token for the authenticated user.
            String token = jwtService.generateToken(request.getUsername());

            // ✅ Return token in JSON response.
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(403).body("{\"error\": \"Invalid username or password\"}");
        }
    }

    /**
     * ✅ User Registration:
     * - Registers a new user.
     * - Ensures that duplicate usernames are not allowed.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        Optional<User> existingUser = userService.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Username already exists\"}");
        }

        // ✅ Register and save the new user.
        User user = userService.registerUser(request.getUsername(), request.getPassword(), request.getRole());

        // ✅ Return the created user object (without password for security).
        return ResponseEntity.ok(user);
    }

    /**
     * ✅ Helper class for Login Request.
     */
    private static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    /**
     * ✅ Helper class for Register Request.
     */
    private static class RegisterRequest {
        private String username;
        private String password;
        private String role;

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
    }
}
