package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentDidNotCompleteCourseExceptionTest {

    @Test
    void testConstructorWithStudentIdAndCourseId() {
        String studentId = "student-1414";
        UUID courseId = UUID.randomUUID();
        StudentDidNotCompleteCourseException exception = new StudentDidNotCompleteCourseException(studentId, courseId);
        assertEquals("Student(%s) tried to do an operations on Course(%s) without completing all quizzes first!".formatted(
            studentId, courseId
        ), exception.getMessage());
    }
}
