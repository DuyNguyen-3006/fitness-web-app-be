package com.fitness.activity_service.controller;
import com.fitness.activity_service.application.dto.activity.ActivityRequest;
import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.activity_service.application.interfaces.ActivityInterface;
import com.fitness.common.api.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private final ActivityInterface activityService;

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityResponse>> trackActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.trackActivity(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getUserTrack(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(activityService.getUserTrack(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ApiResponse<ActivityResponse>> getActivityById(
            @PathVariable("activityId") String activityId) {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }
}
