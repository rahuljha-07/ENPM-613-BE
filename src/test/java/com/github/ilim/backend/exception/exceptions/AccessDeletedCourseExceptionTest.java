package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessDeletedCourseExceptionTest {

    @Test
    void testConstructorWithCourseId() {
        UUID courseId = UUID.randomUUID();
        AccessDeletedCourseException exception = new AccessDeletedCourseException(courseId);
        assertEquals("You can't access a deleted course(" + courseId + ")", exception.getMessage());
    }
}
