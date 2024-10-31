package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.enums.UserRole;

public class UserCannotCreateCourseException extends RuntimeException {
    public UserCannotCreateCourseException(UserRole role) {
        super("User with role %s cannot create a course".formatted(role.name()));
    }
}
