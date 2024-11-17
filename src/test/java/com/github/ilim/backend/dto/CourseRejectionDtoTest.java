package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseRejectionDtoTest {

    @Test
    void testCourseRejectionDtoFields() {
        CourseRejectionDto dto = new CourseRejectionDto();
        dto.setReason("Insufficient content quality.");

        assertEquals("Insufficient content quality.", dto.getReason());
    }
}
