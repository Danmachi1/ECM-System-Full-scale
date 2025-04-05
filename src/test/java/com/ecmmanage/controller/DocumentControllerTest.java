package com.ecmmanage.controller;

import com.ecmmanage.model.Document;
import com.ecmmanage.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ Unit Test cases for `DocumentController`.
 * - Uses JUnit & Mockito for testing controller functionality.
 */
class DocumentControllerTest {

    @Mock
    private DocumentService documentService; // ✅ Mock the service to isolate controller logic.

    @InjectMocks
    private DocumentController documentController; // ✅ Inject the mock service into the controller.

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // ✅ Initialize mocks before each test.
    }

    /**
     * ✅ Test uploading a document.
     */
    @Test
    void testUploadDocument() {
        // ✅ Arrange
        Document document = new Document("Test Title", "test.pdf", "Sample content", "Sample metadata");

        // ✅ Mock service behavior
        when(documentService.saveDocument(any(Document.class))).thenReturn(document);

        // ✅ Act
        ResponseEntity<Document> response = (ResponseEntity<Document>) documentController.uploadDocument(document);

        // ✅ Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Title", response.getBody().getTitle());
    }

    /**
     * ✅ Test fetching all documents.
     */
    @Test
    void testGetAllDocuments() {
        // ✅ Arrange
        Document doc1 = new Document(1L, "Title 1", "file1.pdf", "Content 1", "Metadata 1");
        Document doc2 = new Document(2L, "Title 2", "file2.pdf", "Content 2", "Metadata 2");

        List<Document> documents = Arrays.asList(doc1, doc2);

        // ✅ Mock service behavior
        when(documentService.getAllDocuments()).thenReturn(documents);

        // ✅ Act
        ResponseEntity<List<Document>> response = (ResponseEntity<List<Document>>) documentController.getAllDocuments();

        // ✅ Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Title 1", response.getBody().get(0).getTitle()); // ✅ Accessing title correctly
    }
}
