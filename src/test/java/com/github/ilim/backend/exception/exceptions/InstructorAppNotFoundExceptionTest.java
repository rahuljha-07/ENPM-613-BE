package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructorAppNotFoundExceptionTest {

    @Test
    void testConstructorWithAppId() {
        UUID appId = UUID.randomUUID();
        InstructorAppNotFoundException exception = new InstructorAppNotFoundException(appId);
        assertEquals("Instructor Application not found with ID: " + appId, exception.getMessage());
    }
}
