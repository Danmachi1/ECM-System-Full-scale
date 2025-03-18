// Declares the package where this class is located.
package com.ecmmanage.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * ✅ This enum represents user roles in the system.
 * - Roles define access levels:
 *   - `ADMIN` → Full access (user management, system settings).
 *   - `USER` → Can upload and manage their documents.
 */
public enum Role implements GrantedAuthority { // ✅ Implements GrantedAuthority for Spring Security

    ADMIN("Admin users can manage everything"),
    USER("Regular users can only upload and manage their documents");

    private final String description; // ✅ Adds a description for each role

    /**
     * ✅ Constructor for Role with a description.
     */
    Role(String description) {
        this.description = description;
    }

    /**
     * ✅ Returns the role name as an authority for Spring Security.
     * - Spring Security expects authorities in `String` format.
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    /**
     * ✅ Returns the description of the role.
     */
    public String getDescription() {
        return description;
    }
}
