package com.ecmmanage.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class JwtService {

    // ✅ Secret key should be Base64 encoded in application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}") // 1 hour default
    private long jwtExpirationMs;

    /**
     * ✅ Generate JWT token including username and roles.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())); // ✅ Save roles in token

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // ✅ Username
                .setIssuedAt(new Date())               // ✅ Issued date
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // ✅ Expiry
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // ✅ Sign with secure key
                .compact();
    }

    /**
     * ✅ Validate token matches user and is not expired.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * ✅ Extract username from token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ✅ Extract roles from JWT safely and cleanly.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object rolesObject = claims.get("roles");
            if (rolesObject instanceof List<?>) {
                return ((List<?>) rolesObject).stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList(); // If no roles found
        });
    }

    /**
     * ✅ Extract expiration date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * ✅ Generic extractor for any claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * ✅ Internal: checks expiration.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * ✅ Internal: extract all claims from token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ Decode Base64 secret key and create Key object for signing/verifying.
     */
    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Setter for tests
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setJwtExpirationMs(long jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
    }
}
