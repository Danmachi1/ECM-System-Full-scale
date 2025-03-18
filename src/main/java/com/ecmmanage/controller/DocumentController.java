// This package declaration organizes this class in the "controller" package.
package com.ecmmanage.controller;

// Importing required components.
import com.ecmmanage.model.Document; // The document model class.
import com.ecmmanage.service.DocumentService; // The service that contains business logic.
import org.springframework.beans.factory.annotation.Autowired; // Enables automatic dependency injection.
import org.springframework.http.ResponseEntity; // Helps return HTTP responses.
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for document management.
 * This allows users to upload, retrieve, and search for documents.
 */
@RestController // Marks this class as a REST API controller.
@RequestMapping("/documents") // Defines the base URL path for document-related APIs.
public class DocumentController {

    @Autowired // This tells Spring to inject an instance of DocumentService automatically.
    private DocumentService documentService;

    /**
     * API Endpoint: Upload a new document.
     * 
     * @param document - The document details to be saved.
     * @return ResponseEntity with the saved document details.
     */
    @PostMapping("/upload") // This maps HTTP POST requests to this method.
    public ResponseEntity<Document> uploadDocument(@RequestBody Document document) {
        return ResponseEntity.ok(documentService.saveDocument(document));
    }

    /**
     * API Endpoint: Fetch all stored documents.
     * 
     * @return ResponseEntity containing a list of all documents.
     */
    @GetMapping("/all") // This maps HTTP GET requests to this method.
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }
}
