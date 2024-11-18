package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.enums.PurchaseStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantFindCoursePurchaseExceptionTest {

    @Test
    void testConstructorWithUserIdCourseIdAndStatus() {
        String userId = "user-111";
        UUID courseId = UUID.randomUUID();
        PurchaseStatus status = PurchaseStatus.PENDING;
        CantFindCoursePurchaseException exception = new CantFindCoursePurchaseException(userId, courseId, status);
        assertEquals("Can not find a %s CoursePurchase for User(%s) and Course(%s)".formatted(
            status, userId, courseId
        ), exception.getMessage());
    }
}
