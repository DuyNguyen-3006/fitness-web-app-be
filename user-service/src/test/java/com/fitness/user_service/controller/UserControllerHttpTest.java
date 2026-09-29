package com.fitness.user_service.controller;

import com.fitness.common.api.ApiResponse;
import com.fitness.user_service.application.dto.user.ChangePasswordRequest;
import com.fitness.user_service.application.dto.user.UserRequest;
import com.fitness.user_service.application.dto.user.UserResponse;
import com.fitness.user_service.application.dto.user.UserUpdateRequest;
import com.fitness.user_service.application.exceptions.UserAlreadyExistsException;
import com.fitness.user_service.application.exceptions.IncorrectCurrentPasswordException;
import com.fitness.user_service.application.exceptions.UserNotFoundException;
import com.fitness.user_service.application.interfaces.UserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerHttpTest {
    private UserInterface service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(UserInterface.class);
        mvc = MockMvcBuilders.standaloneSetup(new UserController(service))
                .setControllerAdvice(new UserExceptionHandler())
                .build();
    }

    @Test
    void registrationReturns201AndDuplicateEmailReturns409() throws Exception {
        when(service.registerUser(any(UserRequest.class)))
                .thenReturn(ApiResponse.success("User registered successfully", new UserResponse()))
                .thenThrow(new UserAlreadyExistsException());
        String body = """
                {"email":"new@example.com","password":"password123"}
                """;

        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0]").value("email: duplicate"));
    }

    @Test
    void missingUserReturns404AcrossReadUpdateAndDelete() throws Exception {
        when(service.getUserProfile("missing")).thenThrow(new UserNotFoundException());
        when(service.updateUser(eq("missing"), any(UserUpdateRequest.class)))
                .thenThrow(new UserNotFoundException());
        when(service.deleteUser("missing")).thenThrow(new UserNotFoundException());

        mvc.perform(get("/api/users/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
        mvc.perform(put("/api/users/missing")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"New\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/users/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void passwordChangeValidatesBodyAndRejectsIncorrectCurrentPassword() throws Exception {
        when(service.changePassword(eq("id-1"), any(ChangePasswordRequest.class)))
                .thenThrow(new IncorrectCurrentPasswordException());

        mvc.perform(put("/api/users/id-1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"old-password\",\"newPassword\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        mvc.perform(put("/api/users/id-1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"wrong-password\",\"newPassword\":\"new-password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));
    }

    @Test
    void profileUpdateRejectsEmailAndPasswordFields() throws Exception {
        mvc.perform(put("/api/users/id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@example.com\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(put("/api/users/id-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"new-password\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidRegistrationReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void getAllReturns200WithDefaultResponse() throws Exception {
        when(service.getAllUsers()).thenReturn(ApiResponse.success(List.of()));

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void validateUserReturnsBooleanInsideApiResponse() throws Exception {
        when(service.existByUserId("known"))
                .thenReturn(ApiResponse.success("User validation completed", true));
        when(service.existByUserId("missing"))
                .thenReturn(ApiResponse.success("User validation completed", false));

        mvc.perform(get("/api/users/known/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        mvc.perform(get("/api/users/missing/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }
}
