// ✅ Declares the package where this service belongs.
package com.ecmmanage.service;

// ✅ Import required libraries
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * ✅ This service handles JWT (JSON Web Token) operations:
 *   - Generating JWT tokens for authentication.
 *   - Extracting claims (like username) from tokens.
 *   - Validating tokens to ensure they are not tampered with.
 */
@Service // ✅ Marks this class as a Spring service (bean), making it available for dependency injection.
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    // ✅ Secure secret key (must be at least 256 bits for HS256 algorithm).
    private static final String SECRET_KEY = "changeThisSecretKeyToBeAtLeast256BitsLongOrMoreForSecurity!";

    // ✅ JWT expiration time (10 hours).
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    /**
     * ✅ Generates the signing key from the secret key.
     * - Uses HMAC SHA key for security.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // ✅ Converts the secret key to a valid signing key
    }

    /**
     * ✅ Generates a JWT token.
     *   - Includes username as the subject.
     *   - Includes issued date (`iat`) and expiration date (`exp`).
     *   - Uses HS256 algorithm for signing.
     */
    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username) // ✅ Set the user identifier (subject)
                .setIssuedAt(new Date(System.currentTimeMillis())) // ✅ Token issued at current time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // ✅ Expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ✅ Sign the token
                .compact(); // ✅ Create the compact JWT string

        logger.info("✅ JWT Token Generated for User: " + username);
        return token;
    }

    /**
     * ✅ Extracts the username from a JWT token.
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.warning("⚠️ Failed to extract username from token: " + e.getMessage());
            return null; // Return null if token parsing fails
        }
    }

    /**
     * ✅ Extracts any claim from a JWT token.
     * - Accepts a lambda function `claimsResolver` to specify which claim to extract.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // ✅ Set signing key
                    .build()
                    .parseClaimsJws(token) // ✅ Parse JWT
                    .getBody(); // ✅ Extract claims

            return claimsResolver.apply(claims); // ✅ Apply function to retrieve a specific claim
        } catch (JwtException e) {
            logger.warning("⚠️ JWT Parsing Error: " + e.getMessage());
            throw new RuntimeException("Invalid JWT token.");
        }
    }

    /**
     * ✅ Validates a JWT token.
     *   - Checks if the username inside the token matches the user details.
     *   - Ensures the token is not expired.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);

            if (!isValid) {
                logger.warning("⚠️ JWT Token is Invalid or Expired for user: " + userDetails.getUsername());
            }

            return isValid;
        } catch (Exception e) {
            logger.warning("⚠️ JWT Token validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Checks if the token has expired.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
