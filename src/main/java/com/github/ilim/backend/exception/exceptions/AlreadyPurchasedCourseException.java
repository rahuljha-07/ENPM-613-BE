package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class AlreadyPurchasedCourseException extends RuntimeException {

    public AlreadyPurchasedCourseException(String studentId, UUID courseId) {
        super("Student(%s) already purchased Course(%s)!".formatted(studentId, courseId));
    }

}
