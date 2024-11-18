package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCannotCreateCourseExceptionTest {

    @Test
    void testConstructorWithUserRole() {
        UserRole role = UserRole.STUDENT;
        UserCannotCreateCourseException exception = new UserCannotCreateCourseException(role);
        assertEquals("User with role STUDENT cannot create a course", exception.getMessage());
    }
}
