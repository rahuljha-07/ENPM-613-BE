package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseAlreadyPublishedTest {

    @Test
    void testConstructorWithCourse() {
        Course course = new Course();
        UUID courseId = UUID.randomUUID();
        course.setId(courseId);
        CourseAlreadyPublished exception = new CourseAlreadyPublished(course);
        assertEquals("Course() already published".replace("()", "(" + courseId + ")"), exception.getMessage());
    }
}
