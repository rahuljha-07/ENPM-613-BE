package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockedUserCantSignInExceptionTest {

    @Test
    void testConstructorWithEmail() {
        String email = "blocked.user@example.com";
        BlockedUserCantSignInException exception = new BlockedUserCantSignInException(email);
        assertEquals("User() cannot sign in because user is blocked.".replace("()", "(" + email + ")"), exception.getMessage());
    }
}
