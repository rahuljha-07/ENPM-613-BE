package com.github.ilim.backend.exception.exceptions;

public class AdminCantBeBlockedException extends RuntimeException {

    public AdminCantBeBlockedException(String userId) {
        super("User(%s) has role ADMIN, and admins can not be blocked.".formatted(userId));
    }

}
