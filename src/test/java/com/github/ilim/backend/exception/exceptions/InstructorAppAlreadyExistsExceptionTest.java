package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructorAppAlreadyExistsExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "user-444";
        InstructorAppAlreadyExistsException exception = new InstructorAppAlreadyExistsException(userId);
        assertEquals("A pending Instructor Application already exists for user with ID: " + userId, exception.getMessage());
        assertEquals(userId, exception.getUserId());
    }
}
