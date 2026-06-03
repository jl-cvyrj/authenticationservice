package com.innowise.authentificationservice.service;

import com.innowise.authentificationservice.dto.LoginResponse;
import com.innowise.authentificationservice.exception.InvalidPasswordException;
import com.innowise.authentificationservice.exception.InvalidTokenException;
import com.innowise.authentificationservice.exception.ResourceNotFoundException;
import com.innowise.authentificationservice.exception.ServiceException;
import com.innowise.authentificationservice.exception.UserNotActiveException;

public interface AuthService {

    LoginResponse login(String email, String password) throws ResourceNotFoundException, InvalidPasswordException, UserNotActiveException;

    boolean validateToken(String token);

    LoginResponse refreshToken(String refreshToken) throws InvalidTokenException, ResourceNotFoundException;

    void register(String email, String password, String name, String surname) throws ServiceException;
}
