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
public class User implements UserDetails {

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

    // ✅ Required no-arg constructor for JPA
    public User() {}

    // ✅ All-args constructor for manual creation
    public User(String username, String password, Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ✅ Getters
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    // ✅ Setters (used for updates)
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        if (role == null) throw new IllegalArgumentException("Role cannot be null.");
        this.role = role;
    }

    // ✅ Convert user role to Spring Security authority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // ✅ Account status methods required by UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
