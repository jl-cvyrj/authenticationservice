package com.innowise.authentificationservice.client;

import com.innowise.authentificationservice.dto.UserAuthDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class UserServiceClient {

    private RestTemplate restTemplate;

    UserServiceClient (RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    public UserAuthDto createUser(String email, String name, String surname) {
        String url = userServiceUrl + "/api/users";
        Map<String, String> request = Map.of(
                "email", email,
                "name", name,
                "surname", surname,
                "active", "true"
        );
        return restTemplate.postForObject(url, request, UserAuthDto.class);
    }

    public UserAuthDto getUserById(Long id) {
        String url = userServiceUrl + "/api/users/" + id;
        return restTemplate.getForObject(url, UserAuthDto.class);
    }
}