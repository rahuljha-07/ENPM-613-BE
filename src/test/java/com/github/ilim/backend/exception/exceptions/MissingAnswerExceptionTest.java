package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissingAnswerExceptionTest {

    @Test
    void testConstructorWithAttemptIdAndQuestionId() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        MissingAnswerException exception = new MissingAnswerException(attemptId, questionId);
        assertEquals("In attempt(" + attemptId + ") doesn't have an answer for Question(" + questionId + ")!", exception.getMessage());
    }
}
