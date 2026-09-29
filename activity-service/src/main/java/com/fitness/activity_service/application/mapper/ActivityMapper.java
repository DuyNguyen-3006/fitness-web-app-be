package com.fitness.activity_service.application.mapper;

import com.fitness.activity_service.application.dto.activity.ActivityRequest;
import com.fitness.activity_service.application.dto.activity.ActivityResponse;
import com.fitness.activity_service.domain.models.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Activity toEntity(ActivityRequest request);

    ActivityResponse toResponse(Activity activity);

    List<ActivityResponse> toResponses(List<Activity> activities);
}
