// ✅ This package declaration organizes this class under the "model" package.
package com.ecmmanage.model;

import jakarta.persistence.*;

/**
 * ✅ This class represents a document stored in the ECM system.
 * - It maps to a database table storing document metadata.
 */
@Entity // ✅ Marks this class as a database entity.
@Table(name = "documents") // ✅ Specifies that this entity should be stored in the "documents" table.
public class Document {

    @Id // ✅ Specifies this field as the primary key in the database.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Enables automatic ID generation.
    private Long id; // ✅ Unique identifier for each document.

    @Column(name = "title", nullable = false) // ✅ Title of the document.
    private String title;

    @Column(name = "file_name", nullable = false) // ✅ Defines a column for storing the file name.
    private String fileName;

    @Column(name = "content", columnDefinition = "TEXT") // ✅ Stores the actual content of the document.
    private String content;

    @Column(name = "metadata", columnDefinition = "TEXT") // ✅ Stores additional document metadata.
    private String metadata;

    /**
     * ✅ Default constructor (needed for JPA & Hibernate).
     */
    public Document() {}

    /**
     * ✅ Parameterized constructor for creating a new document object.
     * 
     * @param title The title of the document.
     * @param fileName The name of the file being stored.
     * @param content The actual content of the document.
     * @param metadata Any metadata associated with the file.
     */
    public Document(String title, String fileName, String content, String metadata) {
        this.title = title;
        this.fileName = fileName;
        this.content = content;
        this.metadata = metadata;
    }

    /**
     * ✅ Fully parameterized constructor including ID (used in unit tests or entity mapping).
     * 
     * @param id The unique document ID.
     * @param title The document title.
     * @param fileName The document file name.
     * @param content The content of the document.
     * @param metadata Metadata associated with the document.
     */
    public Document(Long id, String title, String fileName, String content, String metadata) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.content = content;
        this.metadata = metadata;
    }

    // ✅ Getter methods allow other classes to access the values of fields.
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public String getContent() { return content; }
    public String getMetadata() { return metadata; }

    // ✅ Setter methods allow modifying existing document properties.
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setContent(String content) { this.content = content; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    /**
     * ✅ Helper method to **represent the document object as a string**.
     * - Useful for **logging** and **debugging**.
     */
    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 25)) + "..." : "N/A") + '\'' +
                ", metadata='" + metadata + '\'' +
                '}';
    }

}
