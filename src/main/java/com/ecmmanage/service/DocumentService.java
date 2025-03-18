// This package declaration organizes the class under the "service" package.
package com.ecmmanage.service;

// Importing required components.
import com.ecmmanage.model.Document; // The model class for storing document metadata.
import com.ecmmanage.repository.DocumentRepository; // Repository that interacts with the database.
import org.springframework.beans.factory.annotation.Autowired; // Enables automatic dependency injection.
import org.springframework.stereotype.Service; // Marks this class as a service layer.

import java.util.List;

/**
 * This class provides services for managing documents.
 * It contains methods for saving and retrieving documents.
 */
@Service // This annotation marks this class as a service, which means it contains business logic.
public class DocumentService {

    @Autowired // This tells Spring to automatically inject an instance of DocumentRepository.
    private DocumentRepository documentRepository;

    /**
     * Saves a new document to the database.
     */
    public Document saveDocument(Document document) {
        return documentRepository.save(document); // Calls the repository to save the document.
    }

    /**
     * Retrieves all documents from the database.
     */
    public List<Document> getAllDocuments() {
        return documentRepository.findAll(); // Calls the repository to fetch all documents.
    }
}
