// ✅ This package declaration organizes this class in the "controller" package.
package com.ecmmanage.controller;

// ✅ Import required components.

import org.springframework.beans.factory.annotation.Autowired; // Enables dependency injection.
import org.springframework.http.ResponseEntity; // Helps return HTTP responses.
import org.springframework.web.bind.annotation.*;

import com.ecmmanage.model.Document;
import com.ecmmanage.service.DocumentService;

import java.util.List;

/**
 * ✅ REST API Controller for **Document Management**.
 * - Allows users to **upload**, **retrieve**, and **search** documents.
 * - Ensures input validation before processing requests.
 */
@RestController // ✅ Marks this class as a REST API controller.
@RequestMapping("/documents") // ✅ Defines the base URL path for document-related APIs.
public class DocumentController {

    private final DocumentService documentService;

    /**
     * ✅ Constructor-based Dependency Injection
     * - Encourages immutability & testability over field injection.
     */
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * ✅ API Endpoint: **Upload a new document**.
     * - Validates and saves the document in the database.
     *
     * @param document The document details sent in the request body.
     * @return ResponseEntity with the saved document or an error response.
     */
    @PostMapping("/upload") // ✅ Maps HTTP **POST** requests to this method.
    public ResponseEntity<?> uploadDocument(@RequestBody Document document) {
        try {
            // 🛡️ **Input Validation**: Ensure required fields are not empty.
            if (document.getTitle() == null || document.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Title cannot be empty.\"}");
            }
            if (document.getContent() == null || document.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Content cannot be empty.\"}");
            }

            if (document.getFileName() == null || document.getFileName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("File name cannot be empty.");
            }
            if (document.getId() != null) {
                return ResponseEntity.badRequest().body("New documents must not supply an ID.");
            }

            // ✅ Save the document.
            Document savedDocument = documentService.saveDocument(document);
            return ResponseEntity.ok(savedDocument);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Failed to save document: " + e.getMessage() + "\"}");
        }
    }

    /**
     * ✅ API Endpoint: **Retrieve all stored documents**.
     * - Returns a list of all saved documents.
     *
     * @return ResponseEntity containing all documents or an error response.
     */
    @GetMapping("/all") // ✅ Maps HTTP **GET** requests to this method.
    public ResponseEntity<?> getAllDocuments() {
        try {
            List<Document> documents = documentService.getAllDocuments();

            // 🛡️ **Handle case when no documents exist**
            if (documents.isEmpty()) {
                return ResponseEntity.ok().body("{\"message\": \"No documents found.\"}");
            }
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Failed to retrieve documents: " + e.getMessage() + "\"}");
        }
    }
}
