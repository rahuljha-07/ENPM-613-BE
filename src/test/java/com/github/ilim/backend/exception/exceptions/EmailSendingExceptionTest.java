package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailSendingExceptionTest {

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Failed to send email.";
        Throwable cause = new RuntimeException("SMTP server not responding.");
        EmailSendingException exception = new EmailSendingException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Email content is invalid.";
        EmailSendingException exception = new EmailSendingException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
