package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadRequestExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Invalid request parameters.";
        BadRequestException exception = new BadRequestException(message);
        assertEquals(message, exception.getMessage());
    }
}
