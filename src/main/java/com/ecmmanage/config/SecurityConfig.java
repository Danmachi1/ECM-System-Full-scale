// ✅ Declares the package where this security configuration class belongs.
package com.ecmmanage.config;

// ✅ Import necessary Spring Security and JWT components.
import com.ecmmanage.service.JwtService;
import com.ecmmanage.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ✅ Security Configuration:
 * - Configures authentication and authorization.
 * - Defines **JWT-based security** (Stateless).
 * - Registers `JwtAuthenticationFilter`.
 */
@Configuration // ✅ Marks this class as a Spring Security configuration.
public class SecurityConfig {

    private final JwtService jwtService; // ✅ Service for handling JWT operations.
    private final UserService userService; // ✅ Service for loading user details.

    /**
     * ✅ Constructor injection for `JwtService` and `UserService`
     */
    @Autowired
    public SecurityConfig(JwtService jwtService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * ✅ Defines a **password encoder** for securely storing passwords.
     * - Uses BCrypt, which is highly secure.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ Provides the **authentication manager** (Required by Spring Security).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * ✅ Configures security rules for authentication and API protection.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ✅ Disable CSRF protection (not needed for REST APIs).
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll() // ✅ Allow public access to login & registration.
                .anyRequest().authenticated() // 🔒 Protect all other endpoints.
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ Ensure stateless session management.
            )
            .addFilterBefore(
                jwtAuthenticationFilter(), // ✅ Add JWT filter **before** Spring's authentication filter.
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * ✅ Registers `JwtAuthenticationFilter` as a Spring Bean.
     * - Fixes the issue where `JwtAuthenticationFilter` was not recognized.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userService);
    }
}
