// ✅ Declares the package where this filter class belongs.
package com.ecmmanage.config;

// ✅ Import necessary Spring Security and Servlet filter components.
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecmmanage.service.JwtService;
import com.ecmmanage.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ✅ JWT Authentication Filter:
 *   - Extracts and validates JWT tokens from incoming requests.
 *   - Authenticates users based on the token's validity.
 *   - Runs **once per request** (`OncePerRequestFilter`).
 */
@Component // ✅ Marks this class as a Spring-managed bean (automatically detected and injected).
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // ✅ Service for generating and validating JWTs.
    private final UserService userService; // ✅ Service for loading user details.

    /**
     * ✅ Constructor injection for `JwtService` and `UserService`
     */
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService,@Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * ✅ This method is executed once per request to check for JWT authentication.
     * - Extracts JWT from the request header.
     * - Validates the JWT.
     * - Sets authentication in the security context if valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // ✅ Extracts the "Authorization" header from the request.
        String authHeader = request.getHeader("Authorization");

        // ✅ If the Authorization header is missing or does not start with "Bearer ", skip authentication.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Extract the JWT token from the Authorization header (remove "Bearer " prefix).
        String token = authHeader.substring(7);

        // ✅ Extract the username (subject) from the JWT.
        String username = jwtService.extractUsername(token);

        // ✅ If the username is not null and the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username); // ✅ Load user details from the database.

            // ✅ If the token is valid, set authentication in the security context.
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // ✅ Authenticate the user.
            }
        }

        // ✅ Continue the request processing chain.
        chain.doFilter(request, response);
    }
}
