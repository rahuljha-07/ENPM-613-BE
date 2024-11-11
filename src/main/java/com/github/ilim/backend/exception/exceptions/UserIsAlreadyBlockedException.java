package com.github.ilim.backend.exception.exceptions;

public class UserIsAlreadyBlockedException extends RuntimeException {

    public UserIsAlreadyBlockedException(String userId) {
        super("Cannot block User(%s) because it's already blocked!".formatted(userId));
    }

}
