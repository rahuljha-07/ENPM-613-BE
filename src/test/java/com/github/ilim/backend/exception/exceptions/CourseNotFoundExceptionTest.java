package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseNotFoundExceptionTest {

    @Test
    void testConstructorWithCourseId() {
        UUID courseId = UUID.randomUUID();
        CourseNotFoundException exception = new CourseNotFoundException(courseId);
        assertEquals("Course with id '%s' not found".formatted(courseId), exception.getMessage());
    }
}
