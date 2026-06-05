package com.innowise.authentificationservice.service.impl;

import com.innowise.authentificationservice.client.UserServiceClient;
import com.innowise.authentificationservice.dto.LoginResponse;
import com.innowise.authentificationservice.dto.UserAuthDto;
import com.innowise.authentificationservice.entity.Credential;
import com.innowise.authentificationservice.exception.DuplicateResourceException;
import com.innowise.authentificationservice.exception.InvalidPasswordException;
import com.innowise.authentificationservice.exception.InvalidTokenException;
import com.innowise.authentificationservice.exception.ResourceNotFoundException;
import com.innowise.authentificationservice.exception.ServiceException;
import com.innowise.authentificationservice.exception.UserNotActiveException;
import com.innowise.authentificationservice.repository.CredentialRepository;
import com.innowise.authentificationservice.service.AuthService;
import com.innowise.authentificationservice.service.JwtService;
import com.innowise.authentificationservice.util.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;
    private final CredentialRepository credentialRepository;

    public AuthServiceImpl(UserServiceClient userServiceClient, JwtService jwtService, CredentialRepository credentialRepository) {
        this.userServiceClient = userServiceClient;
        this.jwtService = jwtService;
        this.credentialRepository = credentialRepository;
    }

    public LoginResponse login(String email, String password) throws ResourceNotFoundException, InvalidPasswordException, UserNotActiveException {
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!PasswordHasher.checkPassword(password, credential.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        UserAuthDto user = userServiceClient.getUserById(credential.getUserId());

        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + credential.getUserId());
        }

        if (!user.isActive()) {
            throw new UserNotActiveException("User account is deactivated");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().replace("ROLE_", ""));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            jwtService.validateAccessToken(token);
            return true;
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    public LoginResponse refreshToken(String refreshToken) throws InvalidTokenException, ResourceNotFoundException {

        jwtService.validateRefreshToken(refreshToken);

        Long userId = jwtService.extractUserId(refreshToken);

        UserAuthDto user = userServiceClient.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().replace("ROLE_", ""));
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void register(String email, String password, String name, String surname) throws ServiceException {

        if (credentialRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User with email " + email + " already exists");
        }
        try {
            UserAuthDto createdUser = userServiceClient.createUser(email, name, surname);

            String passwordHash = PasswordHasher.hashPassword(password);
            Credential credential = new Credential();
            credential.setEmail(email);
            credential.setPasswordHash(passwordHash);
            credential.setUserId(createdUser.getId());
            credentialRepository.save(credential);

        } catch (Exception e) {
            throw new ServiceException("Failed to register user: " + e.getMessage());
        }
    }
}