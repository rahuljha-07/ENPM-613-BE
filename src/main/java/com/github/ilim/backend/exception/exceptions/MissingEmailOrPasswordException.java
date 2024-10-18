package com.github.ilim.backend.exception.exceptions;

public class MissingEmailOrPasswordException extends RuntimeException {

    public MissingEmailOrPasswordException() {
        super("Both email and password are required");
    }

}
