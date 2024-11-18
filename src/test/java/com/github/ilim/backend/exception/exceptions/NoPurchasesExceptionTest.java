package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoPurchasesExceptionTest {

    @Test
    void testConstructorWithStudentIdAndCourseId() {
        String studentId = "student-666";
        UUID courseId = UUID.randomUUID();
        NoPurchasesException exception = new NoPurchasesException(studentId, courseId);
        assertEquals("We couldn't find any purchase for the student(%s) and course(%s)".formatted(
            studentId, courseId
        ), exception.getMessage());
    }
}
