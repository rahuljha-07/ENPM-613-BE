
package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;

class PaymentRequestDtoTest {

    @Test
    void testCreateRequestDto() {
        // Mock User
        User student = mock(User.class);
        when(student.getId()).thenReturn("user123");

        // Mock Course
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(UUID.randomUUID());
        when(course.getTitle()).thenReturn("Java Course");
        when(course.getDescription()).thenReturn("Comprehensive Java course.");
        when(course.getPrice()).thenReturn(new BigDecimal("299.99"));

        String defaultCurrency = "USD";

        PaymentRequestDto dto = PaymentRequestDto.createRequestDto(student, course, defaultCurrency);

        assertEquals("user123", dto.getUserId());
        assertEquals(course.getId().toString(), dto.getCourseId());
        assertEquals("Java Course", dto.getCourseName());
        assertEquals("Comprehensive Java course.", dto.getCourseDescription());
        assertEquals(299.99, dto.getCoursePrice());
        assertEquals("USD", dto.getCurrency());
    }
}

