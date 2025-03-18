		// Declares the package for this controller.
		package com.ecmmanage.controller;
		
		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.http.ResponseEntity;
		import org.springframework.web.bind.annotation.GetMapping;
		import org.springframework.web.bind.annotation.PathVariable;
		import org.springframework.web.bind.annotation.PostMapping;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.bind.annotation.RequestParam;
		import org.springframework.web.bind.annotation.RestController;
		
		// Import required dependencies.
		import com.ecmmanage.service.BlockchainService;
		
		/**
		 * REST API for blockchain-based document integrity verification.
		 */
		@RestController // Marks this class as a REST controller.
		@RequestMapping("/blockchain") // All API endpoints start with "/blockchain".
		public class BlockchainController {
		
		    @Autowired // Injects the blockchain service.
		    private BlockchainService blockchainService;
		
		    /**
		     * API Endpoint: Generate a cryptographic hash for a document.
		     */
		    @PostMapping("/hash/{documentId}")
		    public ResponseEntity<String> hashDocument(@PathVariable Long documentId, @RequestParam String content) {
		        try {
		            blockchainService.createDocumentHash(documentId, content);
		            return ResponseEntity.ok("Document hash stored successfully.");
		        } catch (Exception e) {
		            return ResponseEntity.status(500).body("Error generating hash: " + e.getMessage());
		        }
		    }
		
		    /**
		     * API Endpoint: Verify a document's integrity by checking its stored hash.
		     */
		    @GetMapping("/verify/{documentId}")
		    public ResponseEntity<Boolean> verifyDocument(@PathVariable Long documentId, @RequestParam String newContent) {
		        try {
		            boolean isValid = blockchainService.verifyDocumentIntegrity(documentId, newContent);
		            return ResponseEntity.ok(isValid);
		        } catch (Exception e) {
		            return ResponseEntity.status(500).body(false);
		        }
		    }
		}
