package com.fitness.activity_service.controller;

import com.fitness.activity_service.application.dto.activity.ActivityRequest;
import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.activity_service.application.interfaces.ActivityInterface;
import com.fitness.common.api.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private final ActivityInterface activityService;

    @PostMapping
    public ResponseEntity<?> trackActivity(
            @RequestBody ActivityRequest request,
            @RequestHeader("X-User-ID") String userId) {
        if (userId != null) {
            request.setUserId(userId);
        }

        ApiResponse<ActivityResponse> response = activityService.trackActivity(request);
        if (!response.success()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response.data());
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserTrack(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(activityService.getUserTrack(userId).data());
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(
            @PathVariable("activityId") String activityId) {
        return ResponseEntity.ok(activityService.getActivityById(activityId).data());
    }



}