package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class CourseModuleNotFoundException extends RuntimeException {

    public CourseModuleNotFoundException(UUID moduleId) {
        super("CourseModule(%s) is not found".formatted(moduleId));
    }
}
