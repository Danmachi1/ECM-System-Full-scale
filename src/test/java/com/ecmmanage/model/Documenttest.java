package com.ecmmanage.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void testDocumentCreationAndGetters() {
        Document document = new Document(1L, "Title Example", "Sample.pdf", "This is the document content", "Metadata Info");

        assertEquals(1L, document.getId());
        assertEquals("Title Example", document.getTitle());
        assertEquals("Sample.pdf", document.getFileName());
        assertEquals("This is the document content", document.getContent());
        assertEquals("Metadata Info", document.getMetadata());
    }

    @Test
    void testSetters() {
        Document document = new Document();
        document.setId(10L);
        document.setTitle("Updated Title");
        document.setFileName("UpdatedFile.txt");
        document.setContent("Updated Content");
        document.setMetadata("Updated Metadata");

        assertEquals(10L, document.getId());
        assertEquals("Updated Title", document.getTitle());
        assertEquals("UpdatedFile.txt", document.getFileName());
        assertEquals("Updated Content", document.getContent());
        assertEquals("Updated Metadata", document.getMetadata());
    }

    @Test
    void testToString() {
        Document document = new Document(5L, "Test Title", "Test.docx", "Sample document content for testing", "Sample Metadata");

        String expectedString = "Document{id=5, title='Test Title', fileName='Test.docx', content='Sample document content f...', metadata='Sample Metadata'}";
        assertEquals(expectedString, document.toString());
    }
}
