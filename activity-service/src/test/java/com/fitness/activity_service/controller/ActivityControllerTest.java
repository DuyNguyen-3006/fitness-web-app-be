package com.fitness.activity_service.controller;

import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.activity_service.application.exceptions.ActivityNotFoundException;
import com.fitness.activity_service.application.interfaces.ActivityInterface;
import com.fitness.activity_service.domain.models.ActivityType;
import com.fitness.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ActivityControllerTest {

    @Test
    void postActivityReturnsCreatedResponse() throws Exception {
        ActivityInterface service = mock(ActivityInterface.class);
        ActivityResponse activity = new ActivityResponse();
        activity.setId("activity-1");
        activity.setUserId("user-1");
        activity.setType(ActivityType.RUNNING);
        when(service.trackActivity(any())).thenReturn(ApiResponse.success("Activity tracked successfully", activity));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ActivityController(service))
                .setControllerAdvice(new ActivityExceptionHandler()).build();

        mvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user-1\",\"type\":\"RUNNING\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("activity-1"));
        verify(service).trackActivity(any());
    }

    @Test
    void postActivityReturnsBadRequestForUnknownUser() throws Exception {
        ActivityInterface service = mock(ActivityInterface.class);
        when(service.trackActivity(any())).thenReturn(ApiResponse.error("Invalid user ID: missing"));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ActivityController(service))
                .setControllerAdvice(new ActivityExceptionHandler()).build();

        mvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"missing\",\"type\":\"RUNNING\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid user ID: missing"));
    }

    @Test
    void getUserTrackReturnsUserActivities() throws Exception {
        ActivityInterface service = mock(ActivityInterface.class);
        ActivityResponse activity = new ActivityResponse();
        activity.setId("activity-1");
        activity.setUserId("user-1");
        when(service.getUserTrack("user-1")).thenReturn(ApiResponse.success(List.of(activity)));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ActivityController(service))
                .setControllerAdvice(new ActivityExceptionHandler()).build();

        mvc.perform(get("/api/activities/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("activity-1"));
        verify(service).getUserTrack("user-1");
    }

    @Test
    void malformedActivityBodyUsesApiResponse() throws Exception {
        ActivityInterface service = mock(ActivityInterface.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ActivityController(service))
                .setControllerAdvice(new ActivityExceptionHandler()).build();

        mvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }

    @Test
    void getActivityByIdReturnsApiResponseAnd404ForMissingId() throws Exception {
        ActivityInterface service = mock(ActivityInterface.class);
        ActivityResponse activity = new ActivityResponse();
        activity.setId("activity-1");
        activity.setUserId("user-1");
        when(service.getActivityById("activity-1"))
                .thenReturn(ApiResponse.success("Activity found", activity));
        when(service.getActivityById("missing")).thenThrow(new ActivityNotFoundException());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ActivityController(service))
                .setControllerAdvice(new ActivityExceptionHandler()).build();

        mvc.perform(get("/api/activities/activity-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("activity-1"));
        mvc.perform(get("/api/activities/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Activity not found"));
    }
}
