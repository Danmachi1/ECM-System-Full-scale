// Declares the package for the repository.
package com.ecmmanage.repository;

// Import the model and Spring Data JPA repository.
import com.ecmmanage.model.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository provides database operations for managing workflow instances.
 */
@Repository // Marks this class as a repository.
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {
    // Spring Data JPA will automatically generate SQL queries for common operations.
}
