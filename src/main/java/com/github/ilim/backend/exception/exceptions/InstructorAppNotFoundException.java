package com.github.ilim.backend.exception.exceptions;

import lombok.Getter;

@Getter
public class InstructorAppNotFoundException extends RuntimeException {

    private final String appId;

    public InstructorAppNotFoundException(String appId) {
        super("Instructor Application not found with ID: " + appId);
        this.appId = appId;
    }
}
