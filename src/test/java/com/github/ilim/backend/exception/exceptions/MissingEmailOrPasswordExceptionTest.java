package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissingEmailOrPasswordExceptionTest {

    @Test
    void testConstructor() {
        MissingEmailOrPasswordException exception = new MissingEmailOrPasswordException();
        assertEquals("Both email and password are required", exception.getMessage());
    }
}
