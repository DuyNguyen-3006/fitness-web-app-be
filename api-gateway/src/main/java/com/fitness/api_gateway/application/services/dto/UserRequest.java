package com.fitness.api_gateway.application.services.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String keycloakId;
    private String firstName;
    private String lastName;
}
