package com.github.ilim.backend.exception.exceptions;

import lombok.Getter;

@Getter
public class InstructorAppAlreadyExistsException extends RuntimeException {

    private final String userId;

    public InstructorAppAlreadyExistsException(String userId) {
        super("A pending Instructor Application already exists for user with ID: " + userId);
        this.userId = userId;
    }
}
