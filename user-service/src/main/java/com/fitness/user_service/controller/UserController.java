package com.fitness.user_service.controller;

import com.fitness.common.api.ApiResponse;
import com.fitness.user_service.application.dto.user.ChangePasswordRequest;
import com.fitness.user_service.application.dto.user.UserRequest;
import com.fitness.user_service.application.dto.user.UserResponse;
import com.fitness.user_service.application.dto.user.UserUpdateRequest;
import com.fitness.user_service.application.interfaces.UserInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserInterface userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable("userId") String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("userId") String userId, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable("userId") String userId, @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateUser(@PathVariable("userId") String userId){
        return ResponseEntity.ok(userService.existByUserId(userId));
    }
}
