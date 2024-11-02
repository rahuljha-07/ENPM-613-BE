package com.github.ilim.backend.exception.exceptions;

public class UserAlreadyInstructorException extends RuntimeException {
    public UserAlreadyInstructorException(String userId) {
        super("User(%s) is already an instructor".formatted(userId));
    }
}
