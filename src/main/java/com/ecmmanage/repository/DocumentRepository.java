package com.ecmmanage.repository;

import com.ecmmanage.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing document-related database operations.
 * Extends JpaRepository to provide CRUD functionality for Document entities.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // JpaRepository provides built-in methods like save(), findById(), delete(), etc.
}
