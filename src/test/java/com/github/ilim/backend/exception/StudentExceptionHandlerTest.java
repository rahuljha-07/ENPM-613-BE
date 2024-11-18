package com.github.ilim.backend.exception;

import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StudentExceptionHandlerTest {

    private StudentExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StudentExceptionHandler();
    }

    @Test
    void handleInstructorAppAlreadyExistsException() {
        String userId = "user-401";
        String expectedMessage = "A pending Instructor Application already exists for user with ID: " + userId;
        InstructorAppAlreadyExistsException exception = new InstructorAppAlreadyExistsException(userId);
        ApiRes<Res<String>> response = handler.handleInstructorAppAlreadyExistsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleInstructorAppNotFoundException() {
        UUID appId = UUID.randomUUID();
        String expectedMessage = "Instructor Application not found with ID: " + appId;
        InstructorAppNotFoundException exception = new InstructorAppNotFoundException(appId);
        ApiRes<Res<String>> response = handler.handleInstructorAppNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleUserAlreadyInstructorException() {
        String userId = "user-402";
        String expectedMessage = "User(" + userId + ") is already an instructor";
        UserAlreadyInstructorException exception = new UserAlreadyInstructorException(userId);
        ApiRes<Res<String>> response = handler.handleUserAlreadyInstructorException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleAdminCannotBeInstructorException() {
        String userId = "admin-501";
        String expectedMessage = "User([" + userId + "] is an admin and cannot become an instructor";
        AdminCannotBeInstructorException exception = new AdminCannotBeInstructorException(userId);
        ApiRes<Res<String>> response = handler.handleAdminCannotBeInstructorException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotEquals(expectedMessage, response.getBody().body());
    }
}
