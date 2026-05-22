package org.ecommerce.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JWTUtil {

    private static final Key SECRET_KEY;
    private static final long EXPIRATION_MS = 24L * 60 * 60 * 1000; // 24h

    static {
        String secret = EnvConfig.get(EnvConfig.ENV_JWT_SECRET, "jwt.secret",
                                      "fashionhub-super-secret-key-change-me-please");
        // Pad to at least 32 bytes for HMAC-SHA256
        while (secret.getBytes(StandardCharsets.UTF_8).length < 32) secret += "x";
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(int userId, String email, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static int extractUserId(String token) {
        return Integer.parseInt(validateToken(token).getSubject());
    }

    public static String extractRole(String token) {
        return (String) validateToken(token).get("role");
    }
}
