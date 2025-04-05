package com.ecmmanage.config;

import com.ecmmanage.service.JwtService;
import com.ecmmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ✅ Full Spring Security configuration using JWT and Role-based access control.
 */
@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public SecurityConfig(JwtService jwtService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // BCrypt/Argon2 support
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // ✅ Allow login & registration without authentication
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()

                // ✅ Only ADMIN can access dev/cleanup
                .requestMatchers(HttpMethod.DELETE, "/auth/dev/cleanup").hasRole("ADMIN")

                // ✅ All authenticated users can view their own info
                .requestMatchers("/api/user/me").authenticated()

                // ✅ Only ADMIN can access all users
                .requestMatchers("/api/user/all").hasRole("ADMIN")

                // ✅ ADMIN can delete/update users
                .requestMatchers("/api/user/delete/**", "/api/user/update/**").hasRole("ADMIN")

                // ✅ All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userService);
    }
}
