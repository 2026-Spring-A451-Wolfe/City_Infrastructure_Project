<<<<<<< HEAD
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: JwtUtil.java                                                        *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Provides methods for generating, validating, and parsing JSON    *
 *              Web Tokens used for secure authentication.                       *
 * Author: Sophina Nichols                                                       *
 * Date Last Modified: 03/03/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/* JwtUtil is a utility class for creating and reading JSON Web Tokens (JWTs).
 * JWTs are used in this system to authenticate users after login. When a user
 * logs in successfully, a token is generated and returned to the frontend. The
 * frontend then sends this token in the Authorization header of every protected
 * request as: "Bearer <token>". 
 */

public class JwtUtil {

    private static final long EXPIRATION_MS = 86400000; // 24 hours

    private static Key getSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isEmpty()) {
            throw new RuntimeException("JWT_SECRET is not set in environment variables!");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSecretKey())
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static long getUserId(String token) {
        return Long.parseLong(validateToken(token).getSubject());
    }

    public static String getRole(String token) {
        return (String) validateToken(token).get("role");
    }
=======
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: JwtUtil.java                                                        *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Provides methods for generating, validating, and parsing JSON    *
 *              Web Tokens used for secure authentication.                       *
 * Author: Sophina Nichols                                                       *
 * Edited By:                                                                    *
 * Hector Maes - 04/02/2026                                                      *
 * Date Last Modified: 04/02/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/* JwtUtil is a utility class for creating and reading JSON Web Tokens (JWTs).
 * JWTs are used in this system to authenticate users after login. When a user
 * logs in successfully, a token is generated and returned to the frontend. The
 * frontend then sends this token in the Authorization header of every protected
 * request as: "Bearer <token>". 
 */

public class JwtUtil {

    private static final long EXPIRATION_MS = 86400000; // Token expiration time (24 hours)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // NOTE: This should eventually be loaded from an environment variable, rather than being
    // generated at runtime (a new key on restart invalidates all tokens!!)

    public static String generateToken(long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
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

    // Extracts user ID from a validated JWT token
    public static long getUserId(String token) {
        return Long.parseLong(validateToken(token).getSubject());
    }

    // Extracts user's role from a validated JWT token.
    public static String getRole(String token) {
        return (String) validateToken(token).get("role");
    }
>>>>>>> servlet-refactor
}