package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(UUID courseId) {
        super("Course with id '%s' not found".formatted(courseId));
    }

}
