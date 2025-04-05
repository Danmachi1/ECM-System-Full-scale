package com.ecmmanage.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Unit tests for the Role enum.
 */
class RoleTest {

    @Test
    void testRoleNames() {
        assertEquals("ROLE_ADMIN", Role.ADMIN.getAuthority());
        assertEquals("ROLE_USER", Role.USER.getAuthority());
    }

    @Test
    void testRoleDescriptions() {
        assertEquals("Admin users can manage everything", Role.ADMIN.getDescription());
        assertEquals("Regular users can only upload and manage their documents", Role.USER.getDescription());
    }

    @Test
    void testRoleEnumValues() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        assertTrue(roles[0] == Role.ADMIN);
        assertTrue(roles[1] == Role.USER);
    }
}
