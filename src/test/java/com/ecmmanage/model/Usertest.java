package com.ecmmanage.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for the User class.
 */
class UserTest {

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        // Creating test users before each test.
        adminUser = new User("adminUser", "securePass123", Role.ADMIN);
        regularUser = new User("regularUser", "userPass456", Role.USER);
    }

    @Test
    void testUserCreation() {
        assertNotNull(adminUser);
        assertEquals("adminUser", adminUser.getUsername());
        assertEquals("securePass123", adminUser.getPassword());
        assertEquals(Role.ADMIN, adminUser.getRole());

        assertNotNull(regularUser);
        assertEquals("regularUser", regularUser.getUsername());
        assertEquals("userPass456", regularUser.getPassword());
        assertEquals(Role.USER, regularUser.getRole());
    }

    @Test
    void testUserAuthorities() {
        Collection<? extends GrantedAuthority> adminAuthorities = adminUser.getAuthorities();
        Collection<? extends GrantedAuthority> userAuthorities = regularUser.getAuthorities();

        assertNotNull(adminAuthorities);
        assertEquals(1, adminAuthorities.size());
        assertEquals("ROLE_ADMIN", adminAuthorities.iterator().next().getAuthority());

        assertNotNull(userAuthorities);
        assertEquals(1, userAuthorities.size());
        assertEquals("ROLE_USER", userAuthorities.iterator().next().getAuthority());
    }

    @Test
    void testAccountStatus() {
        assertTrue(adminUser.isAccountNonExpired());
        assertTrue(adminUser.isAccountNonLocked());
        assertTrue(adminUser.isCredentialsNonExpired());
        assertTrue(adminUser.isEnabled());

        assertTrue(regularUser.isAccountNonExpired());
        assertTrue(regularUser.isAccountNonLocked());
        assertTrue(regularUser.isCredentialsNonExpired());
        assertTrue(regularUser.isEnabled());
    }

    @Test
    void testNullValues() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User(null, null, null);
        });

        assertEquals("Role cannot be null.", exception.getMessage());
    }


    @Test
    void testSetters() {
        adminUser.setUsername("newAdmin");
        adminUser.setPassword("newSecurePass");
        
        assertEquals("newAdmin", adminUser.getUsername());
        assertEquals("newSecurePass", adminUser.getPassword());
    }
}
