package com.fitness.user_service.application.services;

import com.fitness.common.api.ApiResponse;
import com.fitness.user_service.application.dto.user.UserRequest;
import com.fitness.user_service.application.dto.user.UserResponse;
import com.fitness.user_service.application.exceptions.UserAlreadyExistsException;
import com.fitness.user_service.application.mapper.UserMapper;
import com.fitness.user_service.domain.models.User;
import com.fitness.user_service.infrastructure.config.PasswordConfig;
import com.fitness.user_service.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceRegistrationTest {

    @Test
    void registrationStoresEncodedPasswordAndMapsResponse() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder encoder = new PasswordConfig().passwordEncoder();
        UserService service = new UserService(repository, Mappers.getMapper(UserMapper.class), encoder);
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");
        request.setFirstName("Test");

        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<UserResponse> result = service.registerUser(request);

        User saved = verifyAndCaptureSavedUser(repository);
        assertNotEquals(request.getPassword(), saved.getPassword());
        assertTrue(encoder.matches(request.getPassword(), saved.getPassword()));
        assertTrue(result.success());
        assertEquals("test@example.com", result.data().getEmail());
        assertEquals("Test", result.data().getFirstName());
        assertNotNull(result.data().getCreatedAt());
        assertEquals(saved.getCreatedAt(), result.data().getCreatedAt());
        assertEquals(saved.getUpdatedAt(), result.data().getUpdatedAt());
        String responseJson = new ObjectMapper().writeValueAsString(result);
        assertFalse(responseJson.contains("password"));
        assertFalse(responseJson.contains(saved.getPassword()));
    }

    private User verifyAndCaptureSavedUser(UserRepository repository) {
        org.mockito.ArgumentCaptor<User> captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void duplicateEmailIsRejectedBeforeSaving() {
        UserRepository repository = mock(UserRepository.class);
        UserService service = new UserService(repository, Mappers.getMapper(UserMapper.class), new PasswordConfig().passwordEncoder());
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain-password");
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.registerUser(request));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void optionalResponseFieldsAreOmittedWhenNotSet() {
        String json = new ObjectMapper().writeValueAsString(ApiResponse.success("ok", "value"));

        assertFalse(json.contains("errors"));
        assertFalse(json.contains("timestamp"));
    }
}
