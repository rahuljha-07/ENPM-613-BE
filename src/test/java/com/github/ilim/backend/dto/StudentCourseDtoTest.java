package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.exception.exceptions.CantCreatePublicCourseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentCourseDtoTest {

    @Test
    void testFrom_Success() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Test Course");
        course.setThumbnailUrl("http://example.com/thumbnail.jpg");
        course.setDescription("Course Description");
        course.setInstructor(new com.github.ilim.backend.entity.User());
        course.setPrice(new BigDecimal("99.99"));
        course.setStatus(CourseStatus.PUBLISHED);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        CourseModule module = new CourseModule();
        module.setId(UUID.randomUUID());
        module.setTitle("Module 1");
        module.setDescription("Module Description");
        module.setOrderIndex(1);
        module.setCourse(course);
        course.setCourseModules(java.util.List.of(module));

        StudentCourseDto dto = StudentCourseDto.from(course);

        assertNotNull(dto);
        assertEquals(course.getId(), dto.getId());
        assertEquals(course.getTitle(), dto.getTitle());
        assertEquals(course.getThumbnailUrl(), dto.getThumbnailUrl());
        assertEquals(course.getDescription(), dto.getDescription());
        assertEquals(course.getInstructor(), dto.getInstructor());
        assertEquals(course.getPrice(), dto.getPrice());
        assertEquals(course.getStatus(), dto.getStatus());
        assertEquals(course.getCreatedAt(), dto.getCreatedAt());
        assertEquals(course.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(1, dto.getModules().size());
    }

    @Test
    void testFrom_CantCreatePublicCourseException() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Test Course");
        course.setStatus(CourseStatus.DRAFT); // Not PUBLISHED

        assertThrows(CantCreatePublicCourseException.class, () -> {
            StudentCourseDto.from(course);
        });
    }
}
