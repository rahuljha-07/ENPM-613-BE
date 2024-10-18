package com.github.ilim.backend.exception.exceptions;

public class MissingBirthdateException extends RuntimeException {
    public MissingBirthdateException() {
        super("Error: User Birthdate is Missing!");
    }
}
