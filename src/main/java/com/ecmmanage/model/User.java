package com.ecmmanage.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/**
 * ✅ This class represents a user in the system.
 * Implements UserDetails for Spring Security authentication.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails { // ✅ Implements UserDetails for authentication

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Auto-generates user ID
    private Long id;

    @Column(name = "username", unique = true, nullable = false) // ✅ Username must be unique
    private String username;

    @Column(name = "password", nullable = false) // ✅ Password is required
    private String password;

    @Enumerated(EnumType.STRING) // ✅ Stores role as a String in DB
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * ✅ Default constructor (needed for JPA)
     */
    public User() {}

    /**
     * ✅ Constructor to create a user
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * ✅ Getters
     */
    public Long getId() { return id; }
    
    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    public Role getRole() { return role; }

    /**
     * ✅ Returns authorities (roles) for Spring Security authentication.
     * - Uses SimpleGrantedAuthority instead of lambda.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * ✅ Spring Security account status methods
     * These methods allow for enabling/disabling users.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // ✅ Account never expires
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ✅ Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // ✅ Credentials never expire
    }

    @Override
    public boolean isEnabled() {
        return true; // ✅ Account is enabled
    }
}
