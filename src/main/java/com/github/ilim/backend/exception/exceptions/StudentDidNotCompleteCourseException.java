package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class StudentDidNotCompleteCourseException extends RuntimeException {

    public StudentDidNotCompleteCourseException(String studentId, UUID courseId) {
        super("Student(%s) tried to do an operations on Course(%s) without completing all quizzes first!"
            .formatted(studentId, courseId));
    }

}
