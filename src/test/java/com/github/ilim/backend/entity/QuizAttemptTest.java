package com.github.ilim.backend.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizAttemptTest {

    @Test
    void testFrom() {
        User user = new User();
        user.setId("user-123");

        Quiz quiz = new Quiz();
        quiz.setId(UUID.randomUUID());

        QuizAttempt attempt = QuizAttempt.from(user, quiz);

        assertEquals(user, attempt.getStudent());
        assertEquals(quiz, attempt.getQuiz());
    }

    @Test
    void testGetStudentAndQuizId() {
        User user = new User();
        user.setId("user-123");

        Quiz quiz = new Quiz();
        quiz.setId(UUID.randomUUID());

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(user);
        attempt.setQuiz(quiz);

        assertEquals("user-123", attempt.getStudentId());
        assertEquals(quiz.getId(), attempt.geQuizId());
    }
}
