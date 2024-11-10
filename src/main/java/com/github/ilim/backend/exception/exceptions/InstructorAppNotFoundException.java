package com.github.ilim.backend.exception.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InstructorAppNotFoundException extends RuntimeException {
    public InstructorAppNotFoundException(UUID appId) {
        super("Instructor Application not found with ID: " + appId);
    }
}
