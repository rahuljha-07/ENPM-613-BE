package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseModuleNotFoundExceptionTest {

    @Test
    void testConstructorWithModuleId() {
        UUID moduleId = UUID.randomUUID();
        CourseModuleNotFoundException exception = new CourseModuleNotFoundException(moduleId);
        assertEquals("CourseModule() is not found".replace("()", "(" + moduleId + ")"), exception.getMessage());
    }
}
