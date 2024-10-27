package com.github.ilim.backend.exception.exceptions;

public class AdminCannotBeInstructorException extends RuntimeException {
    public AdminCannotBeInstructorException(String userId) {
        super("User[%s] is an admin and cannot become an instructor".formatted(userId));
    }
}
