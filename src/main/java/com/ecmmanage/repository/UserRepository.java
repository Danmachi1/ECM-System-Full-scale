// Declares the package for this repository.
package com.ecmmanage.repository;

// Import required components.
import com.ecmmanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * This repository handles user authentication and storage.
 */
@Repository // Marks this class as a repository.
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by username (used for authentication).
    Optional<User> findByUsername(String username);
}
