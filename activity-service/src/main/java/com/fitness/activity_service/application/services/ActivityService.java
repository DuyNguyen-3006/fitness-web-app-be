package com.fitness.activity_service.application.services;

import com.fitness.activity_service.application.dto.activity.ActivityRequest;
import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.activity_service.application.exceptions.ActivityNotFoundException;
import com.fitness.activity_service.application.interfaces.ActivityInterface;
import com.fitness.activity_service.application.mapper.ActivityMapper;
import com.fitness.activity_service.domain.models.Activity;
import com.fitness.activity_service.infrastructure.repositories.ActivityRepository;
import com.fitness.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService implements ActivityInterface {
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private  String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Override
    public ApiResponse<ActivityResponse> trackActivity(ActivityRequest request) {

        ApiResponse<Boolean> validation = userValidationService.validateUser(request.getUserId());
        if (validation == null || !validation.success() || validation.data() == null) {
            throw new IllegalStateException("User validation failed");
        }
        if (!validation.data()) {
            return ApiResponse.error("Invalid user ID: " + request.getUserId());
        }

        Activity activity = activityMapper.toEntity(request);
        Activity saved = activityRepository.save(activity);

        //Publish the activity to RabbitMQ
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, activity);
        } catch (Exception e) {
            return ApiResponse.error("Failed to publish activity to RabbitMQ: " + e.getMessage());
        }


        return ApiResponse.success("Activity tracked successfully", activityMapper.toResponse(saved));
    }

    @Override
    public ApiResponse<List<ActivityResponse>> getUserTrack(String userId) {
        List<ActivityResponse> activities = activityMapper.toResponses(
                activityRepository.findByUserIdOrderByStartTimeDesc(userId));
        return ApiResponse.success(activities);
    }

    @Override
    public ApiResponse<ActivityResponse> getActivityById(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new);
        return ApiResponse.success("Activity found", activityMapper.toResponse(activity));
    }
}
