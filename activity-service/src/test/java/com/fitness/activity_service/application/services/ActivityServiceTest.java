package com.fitness.activity_service.application.services;

import com.fitness.activity_service.application.dto.activity.ActivityRequest;
import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.activity_service.application.exceptions.ActivityNotFoundException;
import com.fitness.activity_service.application.mapper.ActivityMapper;
import com.fitness.activity_service.domain.models.Activity;
import com.fitness.activity_service.domain.models.ActivityType;
import com.fitness.activity_service.infrastructure.repositories.ActivityRepository;
import com.fitness.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActivityServiceTest {

    @Test
    void trackActivitySavesRequestAndReturnsSavedData() {
        ActivityRepository repository = mock(ActivityRepository.class);
        UserValidationService validationService = mock(UserValidationService.class);
        when(validationService.validateUser("user-1")).thenReturn(ApiResponse.success(true));
        ActivityService service = new ActivityService(repository, Mappers.getMapper(ActivityMapper.class), validationService);
        ActivityRequest request = new ActivityRequest();
        request.setId("client-id");
        request.setUserId("user-1");
        request.setType(ActivityType.RUNNING);
        request.setDuration(30);
        request.setCaloriesBurned(250);
        request.setStartTime(LocalDateTime.of(2026, 9, 27, 9, 0));
        request.setAdditionalMetrics(Map.of("distanceKm", 5));

        when(repository.save(any(Activity.class))).thenAnswer(invocation -> {
            Activity activity = invocation.getArgument(0);
            assertNull(activity.getId());
            activity.setId("generated-id");
            activity.setCreatedAt(LocalDateTime.of(2026, 9, 27, 9, 1));
            return activity;
        });

        ApiResponse<ActivityResponse> result = service.trackActivity(request);
        ActivityResponse response = result.data();

        ArgumentCaptor<Activity> saved = ArgumentCaptor.forClass(Activity.class);
        verify(repository).save(saved.capture());
        assertEquals(true, result.success());
        assertEquals("Activity tracked successfully", result.message());
        assertEquals("user-1", saved.getValue().getUserId());
        assertEquals("generated-id", response.getId());
        assertEquals(ActivityType.RUNNING, response.getType());
        assertEquals(30, response.getDuration());
        assertEquals(250, response.getCaloriesBurned());
        assertEquals(request.getAdditionalMetrics(), response.getAdditionalMetrics());
        assertEquals(LocalDateTime.of(2026, 9, 27, 9, 1), response.getCreatedAt());
    }

    @Test
    void trackActivityDoesNotSaveForUnknownUserOrFailedValidation() {
        ActivityRepository repository = mock(ActivityRepository.class);
        UserValidationService validationService = mock(UserValidationService.class);
        ActivityService service = new ActivityService(repository, Mappers.getMapper(ActivityMapper.class), validationService);
        ActivityRequest request = new ActivityRequest();
        request.setUserId("missing");

        when(validationService.validateUser("missing")).thenReturn(ApiResponse.success(false));
        ApiResponse<ActivityResponse> result = service.trackActivity(request);
        assertEquals(false, result.success());
        assertEquals("Invalid user ID: missing", result.message());
        verify(repository, never()).save(any(Activity.class));

        when(validationService.validateUser("missing")).thenReturn(ApiResponse.error("User service unavailable"));
        assertThrows(IllegalStateException.class, () -> service.trackActivity(request));
        verify(repository, never()).save(any(Activity.class));
    }

    @Test
    void getUserTrackReturnsActivitiesInRepositoryOrder() {
        ActivityRepository repository = mock(ActivityRepository.class);
        ActivityService service = new ActivityService(repository, Mappers.getMapper(ActivityMapper.class),
                mock(UserValidationService.class));
        Activity newer = Activity.builder().id("newer").userId("user-1")
                .startTime(LocalDateTime.of(2026, 9, 28, 10, 0)).build();
        Activity older = Activity.builder().id("older").userId("user-1")
                .startTime(LocalDateTime.of(2026, 9, 27, 10, 0)).build();
        when(repository.findByUserIdOrderByStartTimeDesc("user-1")).thenReturn(List.of(newer, older));

        ApiResponse<List<ActivityResponse>> result = service.getUserTrack("user-1");

        assertEquals(true, result.success());
        assertEquals(List.of("newer", "older"), result.data().stream().map(ActivityResponse::getId).toList());
        verify(repository).findByUserIdOrderByStartTimeDesc("user-1");
    }

    @Test
    void getActivityByIdMapsFoundActivityAndRejectsMissingId() {
        ActivityRepository repository = mock(ActivityRepository.class);
        ActivityService service = new ActivityService(repository, Mappers.getMapper(ActivityMapper.class),
                mock(UserValidationService.class));
        Activity activity = Activity.builder().id("activity-1").userId("user-1")
                .type(ActivityType.RUNNING).build();
        when(repository.findById("activity-1")).thenReturn(Optional.of(activity));
        when(repository.findById("missing")).thenReturn(Optional.empty());

        ApiResponse<ActivityResponse> result = service.getActivityById("activity-1");

        assertEquals(true, result.success());
        assertEquals("Activity found", result.message());
        assertEquals("activity-1", result.data().getId());
        assertEquals("user-1", result.data().getUserId());
        assertThrows(ActivityNotFoundException.class, () -> service.getActivityById("missing"));
    }
}
