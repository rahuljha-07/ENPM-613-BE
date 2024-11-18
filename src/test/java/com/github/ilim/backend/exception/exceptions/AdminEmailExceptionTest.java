package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminEmailExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Invalid admin email format.";
        AdminEmailException exception = new AdminEmailException(message);
        assertEquals(message, exception.getMessage());
    }
}
