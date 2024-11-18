package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizAttemptsNotFoundExceptionTest {

    @Test
    void testConstructorWithQuizId() {
        UUID quizId = UUID.randomUUID();
        QuizAttemptsNotFoundException exception = new QuizAttemptsNotFoundException(quizId);
        assertEquals("Couldn't find any quiz attempt for Quiz()!".replace("()", "(" + quizId + ")"), exception.getMessage());
    }
}
