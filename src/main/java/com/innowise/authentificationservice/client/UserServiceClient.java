package com.innowise.authentificationservice.client;

import com.innowise.authentificationservice.dto.PaymentCardDTO;
import com.innowise.authentificationservice.dto.UserAuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    public UserAuthDTO createUser(String email, String name, String surname) {
        String url = userServiceUrl + "/api/users";
        Map<String, String> request = Map.of(
                "email", email,
                "name", name,
                "surname", surname,
                "active", "true"
        );
        return restTemplate.postForObject(url, request, UserAuthDTO.class);
    }

    public UserAuthDTO getUserById(Long id) {
        String url = userServiceUrl + "/api/users/" + id;
        return restTemplate.getForObject(url, UserAuthDTO.class);
    }

    public Page<UserAuthDTO> getAllUsers(int page, int size) {
        String url = userServiceUrl + "/api/users?page=" + page + "&size=" + size;
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> body = response.getBody();
        List<UserAuthDTO> users = (List<UserAuthDTO>) body.get("content");
        int totalElements = (int) body.get("totalElements");
        return new PageImpl<>(users, PageRequest.of(page, size), totalElements);
    }

    public UserAuthDTO updateUser(Long id, String name, String surname, String email, boolean active) {
        String url = userServiceUrl + "/api/users/" + id;
        Map<String, Object> request = Map.of(
                "name", name,
                "surname", surname,
                "email", email,
                "active", active
        );
        return restTemplate.exchange(url, HttpMethod.PUT, null, UserAuthDTO.class).getBody();
    }

    public void setActiveStatus(Long id, boolean active) {
        String url = userServiceUrl + "/api/users/" + id + "/active?active=" + active;
        restTemplate.patchForObject(url, null, Void.class);
    }

    public List<PaymentCardDTO> getUserPaymentCards(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId + "/payment-cards";
        PaymentCardDTO[] cards = restTemplate.getForObject(url, PaymentCardDTO[].class);
        return Arrays.asList(cards);
    }

    public void deleteUser(Long id) {
        String url = userServiceUrl + "/api/users/" + id;
        restTemplate.delete(url);
    }

    public PaymentCardDTO createCard(PaymentCardDTO card) {
        String url = userServiceUrl + "/api/payment-cards";
        return restTemplate.postForObject(url, card, PaymentCardDTO.class);
    }

    public PaymentCardDTO getCardById(Long id) {
        String url = userServiceUrl + "/api/payment-cards/" + id;
        return restTemplate.getForObject(url, PaymentCardDTO.class);
    }

    public PaymentCardDTO updateCard(Long id, PaymentCardDTO card) {
        String url = userServiceUrl + "/api/payment-cards/" + id;
        return restTemplate.exchange(url, HttpMethod.PUT, null, PaymentCardDTO.class).getBody();
    }

    public void setCardActiveStatus(Long id, boolean active) {
        String url = userServiceUrl + "/api/payment-cards/" + id + "/active?active=" + active;
        restTemplate.patchForObject(url, null, Void.class);
    }

    public Page<PaymentCardDTO> getAllCards(int page, int size, String holder) {
        String url = userServiceUrl + "/api/payment-cards?page=" + page + "&size=" + size;
        if (holder != null) {
            url += "&holder=" + holder;
        }
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> body = response.getBody();
        List<PaymentCardDTO> cards = (List<PaymentCardDTO>) body.get("content");
        int totalElements = (int) body.get("totalElements");
        return new PageImpl<>(cards, PageRequest.of(page, size), totalElements);
    }
}