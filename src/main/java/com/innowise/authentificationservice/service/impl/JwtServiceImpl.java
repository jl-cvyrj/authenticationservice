package com.innowise.authentificationservice.service.impl;

import com.innowise.authentificationservice.exception.InvalidTokenException;
import com.innowise.authentificationservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration:900000}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration:604800000}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("type", "access");
        return generateToken(claims, email, accessExpiration);
    }

    public String generateRefreshToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return generateToken(claims, email, refreshExpiration);
    }

    public String generateServiceToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "SERVICE");
        claims.put("type", "access");
        return generateToken(claims, "auth-service", accessExpiration);
    }

    private String generateToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String token) throws InvalidTokenException {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                throw new InvalidTokenException("Token is not an access token");
            }
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Access token has expired");
        } catch (MalformedJwtException | SignatureException e) {
            throw new InvalidTokenException("Invalid access token");
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed");
        }
    }

    public void validateRefreshToken(String token) throws InvalidTokenException {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                throw new InvalidTokenException("Token is not a refresh token");
            }
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Refresh token has expired");
        } catch (MalformedJwtException | SignatureException e) {
            throw new InvalidTokenException("Invalid refresh token");
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed");
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}