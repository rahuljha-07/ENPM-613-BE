package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminCannotBeInstructorExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "admin-123";
        AdminCannotBeInstructorException exception = new AdminCannotBeInstructorException(userId);
        assertEquals("User[%s] is an admin and cannot become an instructor".formatted(userId), exception.getMessage());
    }
}
