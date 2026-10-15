package com.fitness.api_gateway.application.services.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private String id;
    private String email;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
