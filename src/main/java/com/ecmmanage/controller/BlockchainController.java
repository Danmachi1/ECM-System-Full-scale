// ✅ Declares the package for this controller.
package com.ecmmanage.controller;

// ✅ Import required dependencies.
import com.ecmmanage.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ REST API Controller:
 * - Provides **blockchain-based document integrity verification**.
 * - Allows users to **store & verify** document hashes.
 */
@RestController // ✅ Marks this class as a REST API controller.
@RequestMapping("/blockchain") // ✅ All API endpoints start with "/blockchain".
public class BlockchainController {

    private final BlockchainService blockchainService; // ✅ Blockchain Service for hash operations.

    /**
     * ✅ Constructor-based dependency injection (Recommended).
     */
    @Autowired
    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    /**
     * ✅ API Endpoint: **Generate and store a cryptographic hash** for a document.
     * - Stores the document hash in the blockchain.
     * 
     * @param documentId The document's unique ID.
     * @param content The raw content of the document.
     * @return Response message confirming success or failure.
     */
    @PostMapping("/hash/{documentId}")
    public ResponseEntity<String> hashDocument(@PathVariable Long documentId, @RequestParam String content) {
        try {
            // 🔹 Input validation: Ensure content is not empty.
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Document content cannot be empty.");
            }

            // 🔹 Call blockchain service to generate & store hash.
            blockchainService.createDocumentHash(documentId, content);
            return ResponseEntity.ok("✅ Document hash stored successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Invalid input: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("⚠️ Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * ✅ API Endpoint: **Verify a document's integrity** by checking its stored hash.
     * - Compares the new document's hash with the stored blockchain hash.
     *
     * @param documentId The document's unique ID.
     * @param newContent The new document content to verify.
     * @return `true` if the document is valid; `false` otherwise.
     */
    @GetMapping("/verify/{documentId}")
    public ResponseEntity<String> verifyDocument(@PathVariable Long documentId, @RequestParam String newContent) {
        try {
            // 🔹 Input validation: Ensure new content is not empty.
            if (newContent == null || newContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Verification content cannot be empty.");
            }

            // 🔹 Check document integrity.
            boolean isValid = blockchainService.verifyDocumentIntegrity(documentId, newContent);
            
            // 🔹 Return meaningful messages.
            return isValid
                ? ResponseEntity.ok("✅ Document integrity verified successfully.")
                : ResponseEntity.ok("❌ Document integrity verification failed! Possible tampering detected.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Invalid request: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("⚠️ Internal Server Error: " + e.getMessage());
        }
    }
}
