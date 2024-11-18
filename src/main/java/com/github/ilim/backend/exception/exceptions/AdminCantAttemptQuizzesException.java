package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class AdminCantAttemptQuizzesException extends RuntimeException {
    public AdminCantAttemptQuizzesException(String userId, UUID quizId) {
        super("User(%s) is admin and admins cannot attempt a quiz, including Quiz(%s)"
            .formatted(userId, quizId));
    }
}
