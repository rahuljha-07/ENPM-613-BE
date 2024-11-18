package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminCantAttemptQuizzesExceptionTest {

    @Test
    void testConstructorWithUserIdAndQuizId() {
        String userId = "admin-456";
        UUID quizId = UUID.randomUUID();
        AdminCantAttemptQuizzesException exception = new AdminCantAttemptQuizzesException(userId, quizId);
        assertEquals("User(%s) is admin and admins cannot attempt a quiz, including Quiz(%s)".formatted(
            userId, quizId
        ),exception.getMessage());
    }
}
