package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class MissingAnswerException extends RuntimeException {
    public MissingAnswerException(UUID attemptId, UUID questionId) {
        super("In attempt(%s) doesn't have an answer for Question(%s)!".formatted(attemptId, questionId));
    }
}
