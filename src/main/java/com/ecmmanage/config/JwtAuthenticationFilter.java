package com.ecmmanage.config;

import java.io.IOException;
import java.util.logging.Logger;

import com.ecmmanage.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

/** Validates bearer tokens and uses the user's current database authorities. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String token = header.substring(7);
                String username = jwtService.extractUsername(token);
                if (username != null) {
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    if (jwtService.validateToken(token, user)) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
                    }
                }
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
                SecurityContextHolder.clearContext();
                LOGGER.fine("Bearer authentication rejected");
            }
        }
        // Do not catch downstream failures or run the chain a second time.
        chain.doFilter(request, response);
    }
}
