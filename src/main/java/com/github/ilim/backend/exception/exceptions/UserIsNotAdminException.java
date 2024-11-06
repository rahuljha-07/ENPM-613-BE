package com.github.ilim.backend.exception.exceptions;

public class UserIsNotAdminException extends RuntimeException {

    public UserIsNotAdminException(String userId) {
        super("User(%s) attempted to do an administration operation while user is not admin".formatted(userId));
    }

}
