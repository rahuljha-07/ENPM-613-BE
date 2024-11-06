package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class UserCantHaveQuizProgress extends RuntimeException {
    public UserCantHaveQuizProgress(String userId, UUID courseId) {
        super("User(%s) cannot have a Quiz Progress of Course(%s) because user is not a student of the course."
            .formatted(userId, courseId)
        );
    }
}