package com.ecmmanage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecmmanage.model.Role;
import com.ecmmanage.model.User;
import com.ecmmanage.repository.UserRepository;

/**
 * Service for handling user authentication and registration.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ✅ Finds a user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * ✅ Registers a new user
     */
    public User registerUser(String username, String password, String role) {
        String encryptedPassword = passwordEncoder.encode(password);
        User user = new User(username, encryptedPassword, Enum.valueOf(Role.class, role));
        return userRepository.save(user);
    }

    /**
     * ✅ Spring Security's `UserDetailsService` requires `loadUserByUsername`
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(), // ✅ This should be hashed
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

}
