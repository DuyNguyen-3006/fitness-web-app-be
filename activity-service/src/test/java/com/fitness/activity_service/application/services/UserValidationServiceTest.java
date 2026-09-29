package com.fitness.activity_service.application.services;

import com.fitness.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidationServiceTest {

    @Test
    void readsValidationResultFromApiResponseData() {
        ClientResponse upstream = ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"success\":true,\"message\":\"User validation completed\",\"data\":false}")
                .build();
        WebClient client = WebClient.builder()
                .baseUrl("http://USER-SERVICE")
                .exchangeFunction(request -> Mono.just(upstream))
                .build();

        ApiResponse<Boolean> result = new UserValidationService(client).validateUser("missing");

        assertTrue(result.success());
        assertFalse(result.data());
        assertEquals("User validation completed", result.message());
    }

    @Test
    void mapsNotFoundToFalseButPropagatesOtherHttpErrors() {
        WebClient notFoundClient = clientReturning(HttpStatus.NOT_FOUND);
        ApiResponse<Boolean> result = new UserValidationService(notFoundClient).validateUser("missing");
        assertTrue(result.success());
        assertFalse(result.data());

        WebClient serverErrorClient = clientReturning(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThrows(org.springframework.web.reactive.function.client.WebClientResponseException.class,
                () -> new UserValidationService(serverErrorClient).validateUser("user-1"));
    }

    private static WebClient clientReturning(HttpStatus status) {
        return WebClient.builder()
                .baseUrl("http://USER-SERVICE")
                .exchangeFunction(request -> Mono.just(ClientResponse.create(status).build()))
                .build();
    }
}
