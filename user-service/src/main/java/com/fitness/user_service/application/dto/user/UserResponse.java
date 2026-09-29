package com.fitness.user_service.application.dto.user;

import com.fitness.user_service.domain.models.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
