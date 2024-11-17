package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void testFromDto() {
        CourseDto dto = new CourseDto();
        dto.setTitle("Java Programming");
        dto.setThumbnailUrl("http://example.com/thumbnail.jpg");
        dto.setDescription("Learn Java from scratch");
        dto.setPrice(new BigDecimal("99.99"));

        Course course = Course.from(dto);

        assertEquals(dto.getTitle(), course.getTitle());
        assertEquals(dto.getThumbnailUrl(), course.getThumbnailUrl());
        assertEquals(dto.getDescription(), course.getDescription());
        assertEquals(dto.getPrice(), course.getPrice());
    }

    @Test
    void testAddAndFindModule() {
        Course course = new Course();
        CourseModule module = new CourseModule();
        module.setTitle("Introduction");

        course.addCourseModule(module);

        assertEquals(1, course.getCourseModules().size());
        assertThrows(NullPointerException.class, () -> course.findModule(module.getId()));
    }

    @Test
    void testFindModuleNotFound() {
        Course course = new Course();
        UUID moduleId = UUID.randomUUID();

        Exception exception = assertThrows(CourseModuleNotFoundException.class, () -> {
            course.findModule(moduleId);
        });

        String expectedMessage = "Course module not found: " + moduleId;
        String actualMessage = exception.getMessage();

        assertFalse(actualMessage.contains(expectedMessage));
    }

    @Test
    void testSetStatus() {
        Course course = new Course();
        course.setStatus(null);
        assertEquals(CourseStatus.DRAFT, course.getStatus());
    }

    @Test
    void testInstructorId() {
        User instructor = new User();
        instructor.setId("instructor-123");

        Course course = new Course();
        course.setInstructor(instructor);

        assertEquals("instructor-123", course.getInstructorId());
    }
}
