package com.innowise.authentificationservice.service;

import com.innowise.authentificationservice.exception.InvalidTokenException;

public interface JwtService {

    String generateAccessToken(Long userId, String email, String role);

    String generateRefreshToken(Long userId, String email);

    public String generateServiceToken();

    void validateAccessToken(String token) throws InvalidTokenException;

    void validateRefreshToken(String token) throws InvalidTokenException;

    String extractEmail(String token);

    Long extractUserId(String token);

    String extractRole(String token);

    String extractTokenType(String token);

    boolean isTokenExpired(String token);
}
