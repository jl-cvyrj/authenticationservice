package com.innowise.authentificationservice.security;

import com.innowise.authentificationservice.service.JwtService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ServiceTokenProvider {

    private final JwtService jwtService;

    @Value("${jwt.access.expiration:900000}")
    private long accessExpiration;

    private volatile String cachedToken;
    private volatile Instant expiresAt;

    public ServiceTokenProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void init() {
        refreshToken();
    }

    public String getToken() {
        if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
            synchronized (this) {
                if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
                    refreshToken();
                }
            }
        }
        return cachedToken;
    }

    private void refreshToken() {
        cachedToken = jwtService.generateServiceToken();
        expiresAt = Instant.now().plusMillis(accessExpiration - 5000);
    }
}