package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.CourseStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantCreatePublicCourseExceptionTest {

    @Test
    void testConstructorWithDeletedCourse() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setDeleted(true);
        CantCreatePublicCourseException exception = new CantCreatePublicCourseException(course);
        assertEquals("Cannot create a PublicCourseDto from a deleted Course(" + course.getId() + ")", exception.getMessage());
    }

    @Test
    void testConstructorWithNonPublishedCourse() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setDeleted(false);
        course.setStatus(CourseStatus.DRAFT);
        CantCreatePublicCourseException exception = new CantCreatePublicCourseException(course);
        assertEquals("Cannot create a PublicCourseDto from a non-published Course(" + course.getId() + ")", exception.getMessage());
    }

    @Test
    void testConstructorWithUnknownReason() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setDeleted(false);
        course.setStatus(null); // Assuming status can be null
        CantCreatePublicCourseException exception = new CantCreatePublicCourseException(course);
        assertEquals("Cannot create a PublicCourseDto from a non-published Course(%s)".formatted(
            course.getId()
        ), exception.getMessage());
    }
}
