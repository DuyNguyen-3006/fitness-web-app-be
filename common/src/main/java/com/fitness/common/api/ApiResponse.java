package com.fitness.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors,
        Instant timestamp
) {
    private static final String DEFAULT_SUCCESS_MESSAGE = "Success";
    private static final String DEFAULT_ERROR_MESSAGE = "Request failed";

    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static ApiResponse<Void> ok() {
        return success(DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error() {
        return error(DEFAULT_ERROR_MESSAGE);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors, Instant.now());
    }
}
