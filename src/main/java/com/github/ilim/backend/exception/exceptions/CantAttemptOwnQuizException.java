package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class CantAttemptOwnQuizException extends RuntimeException {
    public CantAttemptOwnQuizException(String userId, UUID quizId) {
        super("User(%s) can't attempt Quiz(%s) because user is the course instructor and creator of the quiz"
            .formatted(userId, quizId));
    }
}
