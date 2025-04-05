// ✅ Declares the package for this repository.
package com.ecmmanage.repository;

// ✅ Import required components.
import com.ecmmanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ✅ Repository for accessing and managing User data.
 * - Spring Data JPA automatically implements basic CRUD methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ✅ Deletes a user by their username.
     * - Will be automatically implemented by Spring JPA.
     */
    void deleteByUsername(String username);

    /**
     * ✅ Find a user by their unique username.
     */
    Optional<User> findByUsername(String username);

    /**
     * ✅ Check if a user exists by username.
     */
    boolean existsByUsername(String username);
}
