package com.fitness.user_service.application.exceptions;

public class IncorrectCurrentPasswordException extends RuntimeException {
    public IncorrectCurrentPasswordException() {
        super("Current password is incorrect");
    }
}
