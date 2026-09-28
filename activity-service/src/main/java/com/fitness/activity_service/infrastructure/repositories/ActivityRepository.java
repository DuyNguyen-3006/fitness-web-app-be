package com.fitness.activity_service.infrastructure.repositories;

import com.fitness.activity_service.domain.models.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByUserIdOrderByStartTimeDesc(String userId);
}
