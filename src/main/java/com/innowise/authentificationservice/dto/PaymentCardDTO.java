package com.innowise.authentificationservice.dto;

import java.time.LocalDate;

public class PaymentCardDTO {
    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private boolean active;
    private Long userId;

    public PaymentCardDTO() {}

    public PaymentCardDTO(Long id, String number, String holder, LocalDate expirationDate, boolean active, Long userId) {
        this.id = id;
        this.number = number;
        this.holder = holder;
        this.expirationDate = expirationDate;
        this.active = active;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getHolder() { return holder; }
    public void setHolder(String holder) { this.holder = holder; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}