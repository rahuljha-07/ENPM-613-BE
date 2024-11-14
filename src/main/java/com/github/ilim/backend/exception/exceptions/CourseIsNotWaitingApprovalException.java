package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.CourseStatus;

import java.util.UUID;

public class CourseIsNotWaitingApprovalException extends RuntimeException {

    public CourseIsNotWaitingApprovalException(UUID courseId, CourseStatus status) {
        super("Course(%s) is not in a WAIT_APPROVAL status, it is %s".formatted(courseId, status));
    }

}
