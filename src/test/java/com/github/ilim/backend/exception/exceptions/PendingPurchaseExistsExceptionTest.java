package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PendingPurchaseExistsExceptionTest {

    @Test
    void testConstructorWithStudentIdCourseIdAndPurchaseId() {
        String studentId = "student-1313";
        UUID courseId = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();
        PendingPurchaseExistsException exception = new PendingPurchaseExistsException(studentId, courseId, purchaseId);
        assertEquals("Student(%s) already has a pending CoursePurchase(%s) record for Course(%s)!".formatted(
            studentId, purchaseId, courseId
        ), exception.getMessage());
    }
}
