// This package declaration organizes this class under the "model" package.
package com.ecmmanage.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * This class represents a document stored in the ECM system.
 * It maps to a database table where we store document metadata.
 */
@Entity // Marks this class as a database entity.
@Table(name = "documents") // Specifies that this entity should be stored in the "documents" table.
public class Document {

    @Id // Specifies that this field is the primary key in the database.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Enables automatic ID generation.
    private Long id; // Unique identifier for each document.

    @Column(name = "file_name", nullable = false) // Defines a column for storing the file name.
    private String fileName;

    @Column(name = "metadata", columnDefinition = "TEXT") // Stores additional document metadata.
    private String metadata;

    /**
     * Default constructor (needed for JPA).
     */
    public Document() {}

    /**
     * Parameterized constructor for creating a new document object.
     */
    public Document(String fileName, String metadata) {
        this.fileName = fileName;
        this.metadata = metadata;
    }

    // Getter methods allow other classes to read the values of these fields.
    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public String getMetadata() { return metadata; }
}
