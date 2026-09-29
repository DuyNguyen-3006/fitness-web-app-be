package com.fitness.user_service.application.interfaces;

import com.fitness.common.api.ApiResponse;
import com.fitness.user_service.application.dto.user.ChangePasswordRequest;
import com.fitness.user_service.application.dto.user.UserRequest;
import com.fitness.user_service.application.dto.user.UserResponse;
import com.fitness.user_service.application.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserInterface {
    ApiResponse<UserResponse> registerUser(UserRequest request);

    ApiResponse<UserResponse> getUserProfile(String userId);

    ApiResponse<List<UserResponse>> getAllUsers();

    ApiResponse<UserResponse> updateUser(String userId, UserUpdateRequest request);

    ApiResponse<Void> changePassword(String userId, ChangePasswordRequest request);

    ApiResponse<Void> deleteUser(String userId);

    ApiResponse<Boolean> existByUserId(String userId);
}
