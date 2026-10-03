package com.ecmmanage.config;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

import com.ecmmanage.service.JwtService;

import jakarta.servlet.ServletException;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidTokenWithRoleAuthenticatesUser() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.extractUsername("validToken")).thenReturn("testuser");
        doReturn(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))).when(userDetails).getAuthorities();
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken("validToken", userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                   .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
    @Test
    void downstreamFailureIsPropagatedWithoutRetryingTheChain() throws Exception {
        jakarta.servlet.FilterChain chain = mock(jakarta.servlet.FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        doThrow(new ServletException("downstream failure")).when(chain).doFilter(request, response);
        assertThrows(ServletException.class,
            () -> jwtAuthenticationFilter.doFilterInternal(request, response, chain));
        verify(chain, times(1)).doFilter(request, response);
    }
}
