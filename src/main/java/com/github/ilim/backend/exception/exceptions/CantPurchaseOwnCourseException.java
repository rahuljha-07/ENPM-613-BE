package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class CantPurchaseOwnCourseException extends RuntimeException {
    public CantPurchaseOwnCourseException(String studentId, UUID courseId) {
        super("User(%s) cannot buy Course (%s) because user is the instructor of the course!"
            .formatted(studentId, courseId));
    }
}
