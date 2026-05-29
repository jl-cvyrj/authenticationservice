package com.innowise.authentificationservice.controller;

import com.innowise.authentificationservice.client.UserServiceClient;
import com.innowise.authentificationservice.dto.PaymentCardDTO;
import com.innowise.authentificationservice.dto.UserAuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserServiceClient userServiceClient;

    @GetMapping("/users")
    public Page<UserAuthDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userServiceClient.getAllUsers(page, size);
    }

    @GetMapping("/users/{id}")
    public UserAuthDTO getUserById(@PathVariable Long id) {
        return userServiceClient.getUserById(id);
    }

    @PutMapping("/users/{id}")
    public UserAuthDTO updateUser(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String email,
            @RequestParam boolean active) {
        return userServiceClient.updateUser(id, name, surname, email, active);
    }

    @PatchMapping("/users/{id}/activate")
    public void activateUser(@PathVariable Long id) {
        userServiceClient.setActiveStatus(id, true);
    }

    @PatchMapping("/users/{id}/deactivate")
    public void deactivateUser(@PathVariable Long id) {
        userServiceClient.setActiveStatus(id, false);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userServiceClient.deleteUser(id);
    }

    @GetMapping("/users/{userId}/cards")
    public List<PaymentCardDTO> getUserCards(@PathVariable Long userId) {
        return userServiceClient.getUserPaymentCards(userId);
    }

    @GetMapping("/cards")
    public Page<PaymentCardDTO> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String holder) {
        return userServiceClient.getAllCards(page, size, holder);
    }

    @GetMapping("/cards/{id}")
    public PaymentCardDTO getCardById(@PathVariable Long id) {
        return userServiceClient.getCardById(id);
    }

    @PostMapping("/cards")
    public PaymentCardDTO createCard(@RequestBody PaymentCardDTO card) {
        return userServiceClient.createCard(card);
    }

    @PutMapping("/cards/{id}")
    public PaymentCardDTO updateCard(@PathVariable Long id, @RequestBody PaymentCardDTO card) {
        return userServiceClient.updateCard(id, card);
    }

    @PatchMapping("/cards/{id}/activate")
    public void activateCard(@PathVariable Long id) {
        userServiceClient.setCardActiveStatus(id, true);
    }

    @PatchMapping("/cards/{id}/deactivate")
    public void deactivateCard(@PathVariable Long id) {
        userServiceClient.setCardActiveStatus(id, false);
    }
}