package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissingBirthdateExceptionTest {

    @Test
    void testConstructor() {
        MissingBirthdateException exception = new MissingBirthdateException();
        assertEquals("Error: User Birthdate is Missing!", exception.getMessage());
    }
}
