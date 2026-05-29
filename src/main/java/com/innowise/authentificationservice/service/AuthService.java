package com.innowise.authentificationservice.service;

import com.innowise.authentificationservice.client.UserServiceClient;
import com.innowise.authentificationservice.dto.LoginResponse;
import com.innowise.authentificationservice.dto.UserAuthDTO;
import com.innowise.authentificationservice.entity.Credential;
import com.innowise.authentificationservice.exception.*;
import com.innowise.authentificationservice.repository.CredentialRepository;
import com.innowise.authentificationservice.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CredentialRepository credentialRepository;

    public LoginResponse login(String email, String password) throws ResourceNotFoundException, InvalidPasswordException, UserNotActiveException {
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!PasswordHasher.checkPassword(password, credential.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        UserAuthDTO user = userServiceClient.getUserById(credential.getUserId());

        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + credential.getUserId());
        }

        if (!user.isActive()) {
            throw new UserNotActiveException("User account is deactivated");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    public LoginResponse refreshToken(String refreshToken) throws InvalidTokenException, ResourceNotFoundException {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);

        UserAuthDTO user = userServiceClient.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    public LoginResponse register(String email, String password, String name, String surname) throws DuplicateResourceException {
        try {
            UserAuthDTO createdUser = userServiceClient.createUser(email, name, surname);

            String passwordHash = PasswordHasher.hashPassword(password);
            Credential credential = new Credential();
            credential.setEmail(email);
            credential.setPasswordHash(passwordHash);
            credential.setUserId(createdUser.getId());
            credentialRepository.save(credential);

            String accessToken = jwtService.generateAccessToken(createdUser.getId(), email, "USER");
            String refreshToken = jwtService.generateRefreshToken(createdUser.getId(), email);

            return new LoginResponse(accessToken, refreshToken);
        } catch (Exception e) {
            throw new DuplicateResourceException("User with email " + email + " already exists");
        }
    }
}