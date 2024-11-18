package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionOptionNotFoundExceptionTest {

    @Test
    void testConstructorWithQuestionOptionId() {
        UUID questionOptionId = UUID.randomUUID();
        QuestionOptionNotFoundException exception = new QuestionOptionNotFoundException(questionOptionId);
        assertEquals("QuestionOption() is not found".replace("()", "(" + questionOptionId + ")"), exception.getMessage());
    }
}
