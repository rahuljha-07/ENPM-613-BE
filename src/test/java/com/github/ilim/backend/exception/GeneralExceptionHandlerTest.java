package com.github.ilim.backend.exception;

import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.EmailSendingException;
import com.github.ilim.backend.exception.exceptions.UnknownApplicationStatusException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneralExceptionHandlerTest {

    private GeneralExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GeneralExceptionHandler();
    }

    @Test
    void handleBadRequestException() {
        String message = "Invalid request data.";
        BadRequestException exception = new BadRequestException(message);
        ApiRes<Res<String>> response = handler.handleBadRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody().body());
    }

    @Test
    void handleEmailSendingException_WithCause() {
        String message = "SMTP server error.";
        EmailSendingException exception = new EmailSendingException(message, new RuntimeException("Connection timed out."));
        ApiRes<Res<String>> response = handler.handleEmailSendingException(exception);

        String expectedMessage = "Failed to send support issue email. Please try again later.";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleEmailSendingException_WithoutCause() {
        String message = "Email content invalid.";
        EmailSendingException exception = new EmailSendingException(message);
        ApiRes<Res<String>> response = handler.handleEmailSendingException(exception);

        String expectedMessage = "Failed to send support issue email. Please try again later.";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleUnknownApplicationStatusException() {
        String statusString = "UNKNOWN_STATUS";
        String expectedMessage = "No InstructorApplication.Status=" + statusString;
        UnknownApplicationStatusException exception = new UnknownApplicationStatusException(statusString);
        ApiRes<Res<String>> response = handler.handleUnknownApplicationStatusException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }
}
