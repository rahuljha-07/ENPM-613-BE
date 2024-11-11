package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class NoPurchasesException extends RuntimeException {
    public NoPurchasesException(String studentId, UUID courseId) {
        super("We couldn't find any purchase for the student(%s) and course(%s)".formatted(studentId, courseId));
    }
}
