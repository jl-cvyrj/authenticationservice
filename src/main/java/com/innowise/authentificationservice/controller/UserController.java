package com.innowise.authentificationservice.controller;

import com.innowise.authentificationservice.client.UserServiceClient;
import com.innowise.authentificationservice.dto.PaymentCardDTO;
import com.innowise.authentificationservice.dto.UserAuthDTO;
import com.innowise.authentificationservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/me")
    public UserAuthDTO getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        return userServiceClient.getUserById(userId);
    }

    @GetMapping("/cards")
    public List<PaymentCardDTO> getMyCards(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        return userServiceClient.getUserPaymentCards(userId);
    }
}
