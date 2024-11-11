package com.github.ilim.backend.exception.exceptions;

public class CantUpdateBlockedUserException extends RuntimeException {

    public CantUpdateBlockedUserException(String userId) {
        super("Cant update blocked User(%s)!".formatted(userId));
    }

}
