package com.fitness.activity_service.application.interfaces;

import com.fitness.activity_service.application.dto.activity.ActivityRequest;
import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.common.api.ApiResponse;

import java.util.List;

public interface ActivityInterface {
    ApiResponse<ActivityResponse> trackActivity(ActivityRequest request);

    ApiResponse<List<ActivityResponse>> getUserTrack(String userId);

    ApiResponse<ActivityResponse> getActivityById(String activityId);
}
