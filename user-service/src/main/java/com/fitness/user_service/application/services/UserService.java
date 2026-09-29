package com.fitness.user_service.application.services;

import com.fitness.common.api.ApiResponse;
import com.fitness.user_service.application.dto.user.ChangePasswordRequest;
import com.fitness.user_service.application.dto.user.UserRequest;
import com.fitness.user_service.application.dto.user.UserResponse;
import com.fitness.user_service.application.dto.user.UserUpdateRequest;
import com.fitness.user_service.application.exceptions.UserAlreadyExistsException;
import com.fitness.user_service.application.exceptions.IncorrectCurrentPasswordException;
import com.fitness.user_service.application.exceptions.UserNotFoundException;
import com.fitness.user_service.application.interfaces.UserInterface;
import com.fitness.user_service.application.mapper.UserMapper;
import com.fitness.user_service.domain.models.User;
import com.fitness.user_service.infrastructure.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiResponse<UserResponse> registerUser(UserRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        User savedUser = repository.save(user);
        return ApiResponse.success("User registered successfully", userMapper.toResponse(savedUser));
    }

    @Override
    public ApiResponse<UserResponse> getUserProfile(String userId) {
        User user = repository.findById(userId).orElseThrow(UserNotFoundException::new);
        return ApiResponse.success("User found", userMapper.toResponse(user));
    }

    @Override
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.success(userMapper.toResponses(repository.findAll()));
    }

    @Override
    @Transactional
    public ApiResponse<UserResponse> updateUser(String userId, UserUpdateRequest request) {
        User user = repository.findById(userId).orElseThrow(UserNotFoundException::new);
        userMapper.updateEntity(request, user);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = repository.save(user);
        return ApiResponse.success("User updated successfully", userMapper.toResponse(savedUser));
    }

    @Override
    @Transactional
    public ApiResponse<Void> changePassword(String userId, ChangePasswordRequest request) {
        User user = repository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IncorrectCurrentPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        repository.save(user);
        return ApiResponse.success("Password changed successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteUser(String userId) {
        User user = repository.findById(userId).orElseThrow(UserNotFoundException::new);
        repository.delete(user);
        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<Boolean> existByUserId(String userId) {
        return ApiResponse.success("User validation completed", repository.existsById(userId));
    }
}
