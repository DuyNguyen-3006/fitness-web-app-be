package com.fitness.user_service.application.services;

import com.fitness.common.api.ApiResponse;
import com.fitness.user_service.application.dto.user.ChangePasswordRequest;
import com.fitness.user_service.application.dto.user.UserResponse;
import com.fitness.user_service.application.dto.user.UserUpdateRequest;
import com.fitness.user_service.application.exceptions.IncorrectCurrentPasswordException;
import com.fitness.user_service.application.exceptions.UserNotFoundException;
import com.fitness.user_service.application.mapper.UserMapper;
import com.fitness.user_service.domain.models.User;
import com.fitness.user_service.infrastructure.config.PasswordConfig;
import com.fitness.user_service.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceCrudTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final PasswordEncoder encoder = new PasswordConfig().passwordEncoder();
    private final UserService service = new UserService(repository, Mappers.getMapper(UserMapper.class), encoder);

    @Test
    void updateOnlyChangesBasicProfileFields() {
        User existing = user("id-1", "old@example.com", "old-hash");
        existing.setFirstName("Old");
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 1, 10, 0);
        existing.setCreatedAt(createdAt);
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("New");

        ApiResponse<UserResponse> result = service.updateUser("id-1", request);

        assertTrue(result.success());
        assertEquals("New", existing.getFirstName());
        assertEquals("old@example.com", existing.getEmail());
        assertEquals("old-hash", existing.getPassword());
        assertEquals("old@example.com", result.data().getEmail());
        assertEquals(createdAt, result.data().getCreatedAt());
        assertNotNull(result.data().getUpdatedAt());
        assertEquals(existing.getUpdatedAt(), result.data().getUpdatedAt());
        assertFalse(new ObjectMapper().writeValueAsString(result).contains("password"));
        verify(repository).save(existing);
        verify(repository, never()).existsByEmail(any());
    }

    @Test
    void changePasswordRequiresCurrentPasswordAndStoresOnlyEncodedNewPassword() {
        User existing = user("id-1", "old@example.com", encoder.encode("old-password"));
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong-password");
        request.setNewPassword("new-password");

        assertThrows(IncorrectCurrentPasswordException.class, () -> service.changePassword("id-1", request));
        assertTrue(encoder.matches("old-password", existing.getPassword()));
        verify(repository, never()).save(any(User.class));

        request.setCurrentPassword("old-password");
        ApiResponse<Void> result = service.changePassword("id-1", request);
        assertTrue(result.success());
        assertTrue(encoder.matches("new-password", existing.getPassword()));
        assertFalse("new-password".equals(existing.getPassword()));
        assertEquals("old@example.com", existing.getEmail());
        assertNotNull(existing.getUpdatedAt());
        verify(repository).save(existing);
    }

    @Test
    void deleteReturnsDefaultResponseAndDoesNotDeleteMissingUser() {
        User existing = user("id-1", "old@example.com", "old-hash");
        when(repository.findById("id-1")).thenReturn(Optional.of(existing));

        ApiResponse<Void> deleted = service.deleteUser("id-1");
        assertTrue(deleted.success());
        assertEquals("Success", deleted.message());
        assertThrows(UserNotFoundException.class, () -> service.deleteUser("missing"));
        verify(repository).delete(existing);
        verify(repository, times(1)).delete(any(User.class));
    }

    @Test
    void getAllUsesDefaultResponseAndNeverExposesPassword() {
        when(repository.findAll()).thenReturn(List.of(user("id-1", "old@example.com", "secret-hash")));

        ApiResponse<List<UserResponse>> result = service.getAllUsers();

        assertTrue(result.success());
        assertEquals("Success", result.message());
        assertEquals(1, result.data().size());
        String json = new ObjectMapper().writeValueAsString(result);
        assertFalse(json.contains("password"));
        assertFalse(json.contains("secret-hash"));
    }

    private static User user(String id, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}
