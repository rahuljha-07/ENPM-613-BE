package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class QuizAttemptsNotFoundException extends RuntimeException {
    public QuizAttemptsNotFoundException(UUID quizId) {
        super("Couldn't find any quiz attempt for Quiz(%s)!".formatted(quizId));
    }
}
