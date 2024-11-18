package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseProgressDtoTest {

    @Test
    void testCourseProgressDtoRecord() {
        UUID courseId = UUID.randomUUID();
        int completedQuizzes = 5;
        int totalQuizzes = 10;

        CourseProgressDto dto = new CourseProgressDto(courseId, completedQuizzes, totalQuizzes);

        assertEquals(courseId, dto.courseId());
        assertEquals(5, dto.completedQuizzes());
        assertEquals(10, dto.totalQuizzes());
    }
}
