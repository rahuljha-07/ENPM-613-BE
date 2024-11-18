package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserIsNotAdminExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "user-1919";
        UserIsNotAdminException exception = new UserIsNotAdminException(userId);
        assertEquals("User() attempted to do an administration operation while user is not admin".replace("()", "(" + userId + ")"), exception.getMessage());
    }
}
