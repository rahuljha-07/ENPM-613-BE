package com.github.ilim.backend.entity;

import com.github.ilim.backend.enums.PurchaseStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CoursePurchaseTest {

    @Test
    void testCreatePendingPurchase() {
        User student = new User();
        student.setId("student-123");

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setPrice(new BigDecimal("49.99"));

        CoursePurchase purchase = CoursePurchase.createPendingPurchase(student, course);

        assertEquals(student, purchase.getStudent());
        assertEquals(course, purchase.getCourse());
        assertEquals(course.getPrice(), purchase.getPurchasePrice());
        assertEquals(PurchaseStatus.PENDING, purchase.getStatus());
        assertNotNull(purchase.getPurchaseDate());
    }

    @Test
    void testGetStudentAndCourseId() {
        User student = new User();
        student.setId("student-123");

        Course course = new Course();
        course.setId(UUID.randomUUID());

        CoursePurchase purchase = new CoursePurchase();
        purchase.setStudent(student);
        purchase.setCourse(course);

        assertEquals("student-123", purchase.getStudentId());
        assertEquals(course.getId(), purchase.geCourseId());
    }
}
