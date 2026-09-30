package com.fitness.activity_service.application.services;

import com.fitness.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public ApiResponse<Boolean> validateUser(String userId) {
        try {
            ApiResponse<Boolean> response = userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Boolean>>() {})
                    .block();
            return response;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ApiResponse.error("User Not Found: " + userId, null);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ApiResponse.error("Invalid Request", null);
            } else {
                return ApiResponse.error("An error occurred while validating the user", null);
            }
        }
    }
}
