package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.enums.CourseStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseIsNotWaitingApprovalExceptionTest {

    @Test
    void testConstructorWithCourseIdAndStatus() {
        UUID courseId = UUID.randomUUID();
        CourseStatus status = CourseStatus.PUBLISHED;
        CourseIsNotWaitingApprovalException exception = new CourseIsNotWaitingApprovalException(courseId, status);
        assertEquals("Course(%s) is not in a WAIT_APPROVAL status, it is %s".formatted(
            courseId, status
        ), exception.getMessage());
    }
}
