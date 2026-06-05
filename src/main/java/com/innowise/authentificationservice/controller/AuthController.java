package com.innowise.authentificationservice.controller;

import com.innowise.authentificationservice.dto.LoginRequest;
import com.innowise.authentificationservice.dto.LoginResponse;
import com.innowise.authentificationservice.dto.RegisterRequest;
import com.innowise.authentificationservice.exception.InvalidPasswordException;
import com.innowise.authentificationservice.exception.InvalidTokenException;
import com.innowise.authentificationservice.exception.ResourceNotFoundException;
import com.innowise.authentificationservice.exception.ServiceException;
import com.innowise.authentificationservice.exception.UserNotActiveException;
import com.innowise.authentificationservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record RefreshRequest(String refreshToken) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest)
            throws ResourceNotFoundException, InvalidPasswordException, UserNotActiveException {
        LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request)
            throws InvalidTokenException, ResourceNotFoundException {
        LoginResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest)
            throws ServiceException {
        authService.register(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getSurname()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}