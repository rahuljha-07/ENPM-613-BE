package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(UUID quizId) {
        super("Quiz(%s) is not found".formatted(quizId));
    }
}
