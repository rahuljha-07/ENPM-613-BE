package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnknownApplicationStatusExceptionTest {

    @Test
    void testConstructorWithStatusString() {
        String statusString = "UNKNOWN_STATUS";
        UnknownApplicationStatusException exception = new UnknownApplicationStatusException(statusString);
        assertEquals("No InstructorApplication.Status=UNKNOWN_STATUS", exception.getMessage());
    }
}
