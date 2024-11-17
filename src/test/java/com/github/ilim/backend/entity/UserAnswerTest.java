package com.github.ilim.backend.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAnswerTest {

    @Test
    void testFrom() {
        Question question = new Question();
        question.setId(UUID.randomUUID());

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(UUID.randomUUID());

        UserAnswer answer = UserAnswer.from(question, attempt);

        assertEquals(question, answer.getQuestion());
        assertEquals(attempt, answer.getAttempt());
    }
}
