package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class UserHasNoAccessToCourseException extends RuntimeException {
    public UserHasNoAccessToCourseException(String userId, UUID courseId) {
        super("User with id '%s' doesn't have access to course with id '%s'".formatted(userId, courseId));
    }
}
