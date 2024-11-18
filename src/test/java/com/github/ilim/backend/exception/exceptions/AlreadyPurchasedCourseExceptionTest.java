package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlreadyPurchasedCourseExceptionTest {

    @Test
    void testConstructorWithStudentIdAndCourseId() {
        String studentId = "student-321";
        UUID courseId = UUID.randomUUID();
        AlreadyPurchasedCourseException exception = new AlreadyPurchasedCourseException(studentId, courseId);
        assertEquals("Student(%s) already purchased Course(%s)!".formatted(
            studentId, courseId
        ), exception.getMessage());
    }
}
