package com.github.ilim.backend.exception.exceptions;

public class BlockedUserCantSignInException extends RuntimeException {

    public BlockedUserCantSignInException(String email) {
        super("User(%s) cannot sign in because user is blocked.".formatted(email));
    }

}
