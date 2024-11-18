package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizNotFoundExceptionTest {

    @Test
    void testConstructorWithQuizId() {
        UUID quizId = UUID.randomUUID();
        QuizNotFoundException exception = new QuizNotFoundException(quizId);
        assertEquals("Quiz() is not found".replace("()", "(" + quizId + ")"), exception.getMessage());
    }
}
