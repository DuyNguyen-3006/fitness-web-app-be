package com.fitness.ai_service.controller;

import com.fitness.ai_service.application.dto.RecommendationResponse;
import com.fitness.ai_service.application.interfaces.RecommendationInterface;
import com.fitness.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationInterface recommendationService;

    public RecommendationController(RecommendationInterface recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getUserRecommendation(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<ApiResponse<RecommendationResponse>> getServiceRecommendation(
            @PathVariable("activityId") String activityId) {
        return ResponseEntity.ok(recommendationService.getServiceRecommendation(activityId));
    }
}
