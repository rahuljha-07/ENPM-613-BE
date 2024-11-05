package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class CantDeleteAttemptedQuiz extends RuntimeException {
    public CantDeleteAttemptedQuiz(String instructorId, UUID quizId, UUID quizAttemptId) {
        super("User(%s) cannot delete Quiz(%s) because some student attempted it in QuizAttemptId(%s)"
            .formatted(instructorId, quizId, quizAttemptId));
    }
}
