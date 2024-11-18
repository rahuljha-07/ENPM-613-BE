package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAlreadyInstructorExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "user-1515";
        UserAlreadyInstructorException exception = new UserAlreadyInstructorException(userId);
        assertEquals("User() is already an instructor".replace("()", "(" + userId + ")"), exception.getMessage());
    }
}
