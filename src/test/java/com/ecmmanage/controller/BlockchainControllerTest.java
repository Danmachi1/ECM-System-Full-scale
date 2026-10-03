package com.ecmmanage.controller;

// ✅ Import required dependencies.
import com.ecmmanage.service.BlockchainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockchainControllerTest {

    private BlockchainController blockchainController;
    private BlockchainService blockchainService;

    @BeforeEach
    void setUp() {
        blockchainService = mock(BlockchainService.class);
        blockchainController = new BlockchainController(blockchainService);
    }

    @Test
    void testHashDocumentSuccess() {
        Long documentId = 1L;
        String content = "Sample document content";

        // ✅ Mock blockchainService to do nothing on success.
        doNothing().when(blockchainService).createDocumentHash(documentId, content);

        ResponseEntity<String> response = blockchainController.hashDocument(documentId, content);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("✅ Document hash stored successfully.", response.getBody());
    }

    @Test
    void testHashDocumentWithEmptyContent() {
        ResponseEntity<String> response = blockchainController.hashDocument(1L, "");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error: Document content cannot be empty.", response.getBody());
    }

    @Test
    void testVerifyDocumentSuccess() {
        Long documentId = 1L;
        String newContent = "Sample document content";

        // ✅ Mock blockchainService to return `true` (document matches).
        when(blockchainService.verifyDocumentIntegrity(documentId, newContent)).thenReturn(true);

        ResponseEntity<String> response = blockchainController.verifyDocument(documentId, newContent);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("✅ Document integrity verified successfully.", response.getBody());
    }

    @Test
    void testVerifyDocumentFailure() {
        Long documentId = 1L;
        String newContent = "Tampered document content";

        // ✅ Mock blockchainService to return `false` (document tampered).
        when(blockchainService.verifyDocumentIntegrity(documentId, newContent)).thenReturn(false);

        ResponseEntity<String> response = blockchainController.verifyDocument(documentId, newContent);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("❌ Document integrity verification failed! Possible tampering detected.", response.getBody());
    }
}
