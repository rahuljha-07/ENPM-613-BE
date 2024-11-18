package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantPurchaseOwnCourseExceptionTest {

    @Test
    void testConstructorWithStudentIdAndCourseId() {
        String studentId = "student-222";
        UUID courseId = UUID.randomUUID();
        CantPurchaseOwnCourseException exception = new CantPurchaseOwnCourseException(studentId, courseId);
        assertEquals("User(%s) cannot buy Course (%s) because user is the instructor of the course!".formatted(studentId, courseId), exception.getMessage());
    }
}
