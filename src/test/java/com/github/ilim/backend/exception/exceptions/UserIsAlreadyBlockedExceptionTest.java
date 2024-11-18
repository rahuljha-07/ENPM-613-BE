package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserIsAlreadyBlockedExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "user-1818";
        UserIsAlreadyBlockedException exception = new UserIsAlreadyBlockedException(userId);
        assertEquals("Cannot block User() because it's already blocked!".replace("()", "(" + userId + ")"), exception.getMessage());
    }
}
