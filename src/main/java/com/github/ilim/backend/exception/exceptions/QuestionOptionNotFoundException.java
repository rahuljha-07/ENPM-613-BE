package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class QuestionOptionNotFoundException extends RuntimeException {
    public QuestionOptionNotFoundException(UUID questionOption) {
        super("QuestionOption(%s) is not found".formatted(questionOption));
    }
}
