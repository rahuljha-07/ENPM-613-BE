package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserHasNoAccessToCourseExceptionTest {

    @Test
    void testConstructorWithUserAndCourseId() {
        User user = new User();
        user.setId("user-1717");
        UUID courseId = UUID.randomUUID();
        UserHasNoAccessToCourseException exception = new UserHasNoAccessToCourseException(user, courseId);
        assertEquals("User with id '%s' doesn't have access to course with id '%s'".formatted(
            user.getId(), courseId
        ), exception.getMessage());
    }
}
