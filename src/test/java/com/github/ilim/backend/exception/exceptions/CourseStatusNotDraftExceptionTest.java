package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.CourseStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseStatusNotDraftExceptionTest {

    @Test
    void testConstructorWithCourse() {
        Course course = new Course();
        UUID courseId = UUID.randomUUID();
        course.setId(courseId);
        course.setStatus(CourseStatus.PUBLISHED);
        CourseStatusNotDraftException exception = new CourseStatusNotDraftException(course);
        assertEquals("Course(%s) must be in DRAFT status, not status=%s".formatted(
            courseId.toString(), course.getStatus()
        ), exception.getMessage());
    }
}
