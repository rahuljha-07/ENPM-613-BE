package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentProcessingExceptionTest {

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Payment gateway timeout.";
        Throwable cause = new RuntimeException("Gateway not responding.");
        PaymentProcessingException exception = new PaymentProcessingException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Invalid payment details.";
        PaymentProcessingException exception = new PaymentProcessingException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
