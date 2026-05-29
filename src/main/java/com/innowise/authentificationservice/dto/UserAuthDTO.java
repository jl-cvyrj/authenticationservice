package com.innowise.authentificationservice.dto;

public class UserAuthDTO {
    private Long id;
    private String email;
    private String role;
    private boolean active;

    public UserAuthDTO(Long id, String email, String role, boolean active) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public UserAuthDTO() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
}