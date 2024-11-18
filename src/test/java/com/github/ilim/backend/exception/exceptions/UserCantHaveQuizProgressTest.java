package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCantHaveQuizProgressTest {

    @Test
    void testConstructorWithUserIdAndCourseId() {
        String userId = "user-1616";
        UUID courseId = UUID.randomUUID();
        UserCantHaveQuizProgress exception = new UserCantHaveQuizProgress(userId, courseId);
        assertEquals("User(%s) cannot have a Quiz Progress of Course(%s) because user is not a student of the course."
            .formatted(userId, courseId), exception.getMessage());
    }
}
