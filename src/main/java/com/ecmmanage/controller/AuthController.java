// ✅ Declares the package for this controller.
package com.ecmmanage.controller;

// ✅ Import necessary Spring Security, JWT, and API components.
import com.ecmmanage.model.User;
import com.ecmmanage.service.JwtService;
import com.ecmmanage.service.UserService;
import com.ecmmanage.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtService jwtService;
    private final UserRepository userRepository; // ✅ Inject UserRepository for cleanup

    /**
     * ✅ Constructor injection for dependencies.
     */
    @Autowired
    public AuthController(
        AuthenticationManager authenticationManager,
        UserService userService,
        JwtService jwtService,
        UserRepository userRepository // ✅ Inject via constructor
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * ✅ Login Endpoint:
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(request.username());
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(403).body(new AuthResponse("Invalid username or password"));
        }
    }

    /**
     * ✅ User Registration:
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        Optional<User> existingUser = userService.findByUsername(request.username());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse("Username already exists"));
        }

        User user = userService.registerUser(request.username(), request.password(), request.role());
        return ResponseEntity.ok(user);
    }

    /**
     * ✅ Dev-only Cleanup Endpoint:
     */
 // ✅ Deletes a user by username. Only accessible to users with role ADMIN.
    @DeleteMapping("/dev/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserByUsername(@RequestParam("username") String username) {
        if (userRepository.existsByUsername(username)) {
            userService.deleteByUsername(username); // ✅ Use service layer with @Transactional
            return ResponseEntity.ok("✅ Deleted user: " + username);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ User not found: " + username);
        }
    }




    /**
     * ✅ DTO for login request.
     */
    public record LoginRequest(
        @NotBlank(message = "Username cannot be empty") String username,
        @NotBlank(message = "Password cannot be empty") String password
    ) {}

    /**
     * ✅ DTO for register request.
     */
    public record RegisterRequest(
        @NotBlank(message = "Username cannot be empty") String username,
        @NotBlank(message = "Password cannot be empty") String password,
        @NotBlank(message = "Role cannot be empty") String role
    ) {}

    /**
     * ✅ DTO for authentication responses.
     */
    public record AuthResponse(String message) {}
}
