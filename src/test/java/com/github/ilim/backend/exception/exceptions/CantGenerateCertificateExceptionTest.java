package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CantGenerateCertificateExceptionTest {

    @Test
    void testConstructorWithCourseAndCause() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        Exception cause = new Exception("Generation failed");
        CantGenerateCertificateException exception = new CantGenerateCertificateException(course, cause);
        assertEquals("Failed to generate a certificate for Course(" + course.getId() + ")", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
