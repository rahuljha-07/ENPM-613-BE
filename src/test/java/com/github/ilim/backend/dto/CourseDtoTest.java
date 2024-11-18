package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseDtoTest {

    @Test
    void testCourseDtoFields() {
        CourseDto dto = new CourseDto();
        dto.setTitle("Java Programming");
        dto.setDescription("Learn Java from scratch.");
        dto.setPrice(new BigDecimal("199.99"));
        dto.setThumbnailUrl("http://example.com/thumbnail.jpg");

        assertEquals("Java Programming", dto.getTitle());
        assertEquals("Learn Java from scratch.", dto.getDescription());
        assertEquals(new BigDecimal("199.99"), dto.getPrice());
        assertEquals("http://example.com/thumbnail.jpg", dto.getThumbnailUrl());
    }
}
