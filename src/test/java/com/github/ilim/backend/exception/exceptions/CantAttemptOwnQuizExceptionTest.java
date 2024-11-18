package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantAttemptOwnQuizExceptionTest {

    @Test
    void testConstructorWithUserIdAndQuizId() {
        String userId = "user-654";
        UUID quizId = UUID.randomUUID();
        CantAttemptOwnQuizException exception = new CantAttemptOwnQuizException(userId, quizId);
        assertEquals("User(%s) can't attempt Quiz(%s) because user is the course instructor and creator of the quiz".formatted(
            userId, quizId
        ), exception.getMessage());
    }
}
