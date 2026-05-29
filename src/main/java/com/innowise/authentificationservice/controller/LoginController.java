package com.innowise.authentificationservice.controller;

import com.innowise.authentificationservice.dto.LoginRequest;
import com.innowise.authentificationservice.dto.LoginResponse;
import com.innowise.authentificationservice.dto.RegisterRequest;
import com.innowise.authentificationservice.exception.*;
import com.innowise.authentificationservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest)
            throws ResourceNotFoundException, InvalidPasswordException, UserNotActiveException {
        LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken)
            throws InvalidTokenException, ResourceNotFoundException {
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest)
            throws DuplicateResourceException {
        LoginResponse response = authService.register(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getName(),
                registerRequest.getSurname()
        );
        return ResponseEntity.ok(response);
    }
}