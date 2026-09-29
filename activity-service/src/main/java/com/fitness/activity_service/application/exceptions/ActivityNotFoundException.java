package com.fitness.activity_service.application.exceptions;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException() {
        super("Activity not found");
    }
}
