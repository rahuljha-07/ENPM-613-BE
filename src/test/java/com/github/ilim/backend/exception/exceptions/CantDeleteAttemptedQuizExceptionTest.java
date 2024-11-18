package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantDeleteAttemptedQuizExceptionTest {

    @Test
    void testConstructorWithInstructorIdQuizIdAndAttemptId() {
        String instructorId = "instructor-987";
        UUID quizId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        CantDeleteAttemptedQuizException exception = new CantDeleteAttemptedQuizException(instructorId, quizId, attemptId);
        assertEquals("User(%s) cannot delete Quiz(%s) because some student attempted it in QuizAttemptId(%s)".formatted(
            instructorId, quizId, attemptId
        ), exception.getMessage());
    }
}
