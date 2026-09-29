package com.fitness.user_service.application.dto.user;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 1, message = "First name cannot be empty")
    private String firstName;

    @Size(min = 1, message = "Last name cannot be empty")
    private String lastName;

    @JsonAnySetter
    public void rejectUnsupportedField(String name, Object value) {
        throw new IllegalArgumentException("Unsupported profile field: " + name);
    }
}
