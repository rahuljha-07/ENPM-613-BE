package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantUpdateBlockedUserExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "user-333";
        CantUpdateBlockedUserException exception = new CantUpdateBlockedUserException(userId);
        assertEquals("Cant update blocked User()!".replace("()", "(" + userId + ")"), exception.getMessage());
    }
}
