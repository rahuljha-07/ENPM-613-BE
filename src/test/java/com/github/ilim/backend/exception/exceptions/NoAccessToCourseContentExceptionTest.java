package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoAccessToCourseContentExceptionTest {

    @Test
    void testConstructorWithUserAndCourseId() {
        User user = new User();
        user.setId("user-555");
        UUID courseId = UUID.randomUUID();
        NoAccessToCourseContentException exception = new NoAccessToCourseContentException(user, courseId);
        assertEquals("User(" + user.getId() + ") cannot access the content of the course(" + courseId + ")", exception.getMessage());
    }

    @Test
    void testConstructorWithNullUserAndCourseId() {
        UUID courseId = UUID.randomUUID();
        NoAccessToCourseContentException exception = new NoAccessToCourseContentException(null, courseId);
        assertEquals("User(null) cannot access the content of the course(" + courseId + ")", exception.getMessage());
    }
}
