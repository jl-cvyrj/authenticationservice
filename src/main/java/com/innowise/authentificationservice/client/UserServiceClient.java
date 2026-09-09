package com.innowise.authentificationservice.client;

import com.innowise.authentificationservice.dto.UserAuthDto;
import com.innowise.authentificationservice.security.ServiceTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class UserServiceClient {

    private RestTemplate restTemplate;
    private final ServiceTokenProvider serviceTokenProvider;

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    UserServiceClient(RestTemplate restTemplate, ServiceTokenProvider serviceTokenProvider) {
        this.restTemplate = restTemplate;
        this.serviceTokenProvider = serviceTokenProvider;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceTokenProvider.getToken());
        return headers;
    }

    public UserAuthDto createUser(String email, String name, String surname) {
        String url = userServiceUrl + "/api/users";
        Map<String, String> request = Map.of(
                "email", email,
                "name", name,
                "surname", surname,
                "active", "true"
        );
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, authHeaders());
        ResponseEntity<UserAuthDto> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, UserAuthDto.class);
        return response.getBody();
    }

    public UserAuthDto getUserById(Long id) {
        String url = userServiceUrl + "/api/users/" + id;
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
        ResponseEntity<UserAuthDto> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, UserAuthDto.class);
        return response.getBody();
    }
}