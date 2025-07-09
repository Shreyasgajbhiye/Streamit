// package com.streamit.api_gateway_service.util;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import java.security.Key;
// import java.util.Date;

// @Component
// public class JwtUtil {

//     @Value("${jwt.secret}")
//     private String secret;

//     public Claims extractClaims(String token) {
//         if (token == null || token.trim().isEmpty()) {
//             throw new JwtException("Token cannot be null or empty");
//         }
        
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSignKey())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     public boolean isTokenValid(String token) {
//         try {
//             Claims claims = extractClaims(token);
//             return !isTokenExpired(claims);
//         } catch (JwtException e) {
//             return false;
//         }
//     }

//     public String extractUserId(String token) {
//         try {
//             Claims claims = extractClaims(token);
//             return claims.getSubject();
//         } catch (JwtException e) {
//             throw new JwtException("Unable to extract user ID from token", e);
//         }
//     }

//     public String extractUsername(String token) {
//         try {
//             Claims claims = extractClaims(token);
//             return claims.get("username", String.class);
//         } catch (JwtException e) {
//             throw new JwtException("Unable to extract username from token", e);
//         }
//     }

//     private boolean isTokenExpired(Claims claims) {
//         Date expiration = claims.getExpiration();
//         return expiration != null && expiration.before(new Date());
//     }

//     private Key getSignKey() {
//         if (secret == null || secret.length() < 32) {
//             throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
//         }
//         return Keys.hmacShaKeyFor(secret.getBytes());
//     }
// }

package com.streamit.api_gateway_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final Key signingKey;
    
    // Constructor-based initialization
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        this.secret = secret;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims extractClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Token cannot be null or empty");
        }
        
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Token is not supported", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Token is malformed", e);
        } catch (SecurityException e) {
            throw new JwtException("Token signature is invalid", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Token is invalid", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !isTokenExpired(claims);
        } catch (JwtException e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public String extractUserId(String token) {
        try {
            Claims claims = extractClaims(token);
            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                throw new JwtException("Token does not contain valid user ID");
            }
            return subject;
        } catch (JwtException e) {
            throw new JwtException("Unable to extract user ID from token: " + e.getMessage(), e);
        }
    }

    public String extractUsername(String token) {
        try {
            Claims claims = extractClaims(token);
            String username = claims.get("username", String.class);
            if (username == null || username.isEmpty()) {
                // Fallback to subject if username claim is not present
                return claims.getSubject();
            }
            return username;
        } catch (JwtException e) {
            throw new JwtException("Unable to extract username from token: " + e.getMessage(), e);
        }
    }

    public Date extractExpiration(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration();
        } catch (JwtException e) {
            throw new JwtException("Unable to extract expiration from token: " + e.getMessage(), e);
        }
    }

    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    // Optional: Method to validate token and get user info in one call
    public String validateTokenAndGetUserId(String token) {
        if (!isTokenValid(token)) {
            throw new JwtException("Token is invalid or expired");
        }
        return extractUserId(token);
    }
}