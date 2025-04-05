// Declare the package that contains this service class.
package com.ecmmanage.service;

// Import necessary libraries.
import org.springframework.stereotype.Service; // Marks this class as a Spring Service Component.
import java.nio.charset.StandardCharsets; // Standard character encoding for string conversion.
import java.security.MessageDigest; // Java class for generating cryptographic hashes.
import java.security.NoSuchAlgorithmException; // Exception thrown when the hashing algorithm is not found.
import java.util.HashMap; // HashMap is used to store key-value pairs.
import java.util.Map; // Map interface represents a collection of key-value pairs.

/**
 * BlockchainService is responsible for managing document hashes for integrity verification.
 * It allows generating, storing, and verifying hashes using the SHA-256 algorithm.
 */
@Service // This annotation makes the class a Spring-managed service component.
public class BlockchainService {

    /**
     * Simulated blockchain ledger: This stores document hashes.
     * Key: Document ID (Long) - Unique identifier for each document.
     * Value: SHA-256 Hash of the document content (String).
     */
    private final Map<Long, String> documentHashes = new HashMap<>();

    /**
     * Generates a cryptographic hash for the given document content and stores it.
     *
     * @param documentId Unique identifier for the document.
     * @param content    The content of the document that needs to be hashed.
     */
    public void createDocumentHash(Long documentId, String content) {
        // Generate a SHA-256 hash from the document content.
        String hash = generateHash(content);

        // Store the generated hash in the documentHashes map with the documentId as the key.
        documentHashes.put(documentId, hash);
    }

    /**
     * Verifies the integrity of a document by comparing the stored hash with a newly computed hash.
     *
     * @param documentId  Unique identifier of the document to verify.
     * @param newContent  The new content of the document for comparison.
     * @return True if the stored hash matches the new content's hash (document is unchanged), False otherwise.
     * @throws IllegalArgumentException if no hash exists for the given document ID.
     */
    public boolean verifyDocumentIntegrity(Long documentId, String newContent) {
        // Retrieve the stored hash from the map based on the provided documentId.
        String storedHash = documentHashes.get(documentId);

        // If there is no stored hash for this document, throw an error.
        if (storedHash == null) {
            throw new IllegalArgumentException("No hash found for document ID: " + documentId);
        }

        // Generate a new hash from the provided newContent.
        String newHash = generateHash(newContent);

        // Compare the stored hash with the newly generated hash and return the result.
        return storedHash.equals(newHash);
    }

    /**
     * Generates a SHA-256 cryptographic hash for a given string input.
     *
     * @param input The string content to hash.
     * @return A hexadecimal string representation of the SHA-256 hash.
     */
    private String generateHash(String input) {
        try {
            // Get an instance of the SHA-256 message digest algorithm.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute the hash of the input string, converting it to a byte array.
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array into a readable hexadecimal string.
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // Convert each byte to a 2-character hexadecimal format.
                hexString.append(String.format("%02x", b));
            }

            // Return the final hashed string in hexadecimal format.
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // If SHA-256 algorithm is not available, throw a runtime exception.
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
