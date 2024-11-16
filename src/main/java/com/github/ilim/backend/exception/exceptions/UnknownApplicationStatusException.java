package com.github.ilim.backend.exception.exceptions;

public class UnknownApplicationStatusException extends RuntimeException {
    public UnknownApplicationStatusException(String statusString) {
        super("No InstructorApplication.Status=%s".formatted(statusString));
    }
}
