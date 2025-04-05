package com.ecmmanage.config;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecmmanage.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ✅ JWT Authentication Filter:
 * - Extracts and validates JWT tokens from incoming requests.
 * - Authenticates users based on the token's validity.
 * - Assigns roles to users from the JWT.
 * - Runs **once per request** (`OncePerRequestFilter`).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            // ✅ Extracts the "Authorization" header from the request.
            String authHeader = request.getHeader("Authorization");

            // ✅ If the Authorization header is missing or does not start with "Bearer ", skip authentication.
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            // ✅ Extract the JWT token from the Authorization header.
            String token = authHeader.substring(7);

            // ✅ Extract the username and roles from the JWT.
            String username = jwtService.extractUsername(token);
            List<String> roles = jwtService.extractRoles(token); // ✅ Extract roles from token.

            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(request, response);
                return;
            }

            // ✅ Load user details from the database.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ✅ Validate the token and assign roles to the user.
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()) // ✅ Assign roles
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            LOGGER.warning("⚠️ Authentication failed: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}
