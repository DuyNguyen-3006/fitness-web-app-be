package com.fitness.ai_service.domain.models;

import lombok.Data;

import java.util.Map;

@Data
public class Activity {
    private String id;
    private String userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private Map<String, Object> additionalMetrics;
}
