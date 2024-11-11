package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.CourseStatus;

public class CourseStatusNotDraftException extends RuntimeException {

    public CourseStatusNotDraftException(Course course) {
        super("Course(%s) must be in %s status, not status=%s".formatted(
            course.getId(), CourseStatus.DRAFT.name(), course.getStatus())
        );
    }

}
