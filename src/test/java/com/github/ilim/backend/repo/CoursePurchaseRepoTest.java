package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PurchaseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CoursePurchaseRepoTest {

    @Autowired
    private CoursePurchaseRepo coursePurchaseRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CourseRepo courseRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setBirthdate(LocalDateTime.now().toLocalDate());
        student = userRepo.save(student); // Use the returned managed entity

        course = new Course();
        course.setTitle("Test Course");
        course.setPrice(new BigDecimal("49.99"));
        course.setDeleted(false);
        course = courseRepo.save(course); // Use the returned managed entity
    }

    @Test
    void testFindByStudentAndCourse() {
        CoursePurchase purchase = new CoursePurchase();
        purchase.setStudent(student);
        purchase.setCourse(course);
        purchase.setStatus(PurchaseStatus.SUCCEEDED);
        purchase.setPurchasePrice(new BigDecimal("49.99"));
        purchase.setPurchaseDate(LocalDateTime.now());

        coursePurchaseRepo.save(purchase);

        List<CoursePurchase> purchases = coursePurchaseRepo.findByStudentAndCourse(student, course, Sort.unsorted());

        assertEquals(1, purchases.size());
        assertEquals(purchase.getId(), purchases.getFirst().getId());
    }

    @Test
    void testFindAllByStudent() {
        CoursePurchase purchase1 = new CoursePurchase();
        purchase1.setStudent(student);
        purchase1.setCourse(course);
        purchase1.setStatus(PurchaseStatus.SUCCEEDED);
        purchase1.setPurchasePrice(new BigDecimal("49.99"));
        purchase1.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(purchase1);

        CoursePurchase purchase2 = new CoursePurchase();
        purchase2.setStudent(student);
        purchase2.setCourse(course);
        purchase2.setStatus(PurchaseStatus.PENDING);
        purchase2.setPurchasePrice(new BigDecimal("49.99"));
        purchase2.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(purchase2);

        List<CoursePurchase> purchases = coursePurchaseRepo.findAllByStudent(student, Sort.unsorted());

        assertEquals(2, purchases.size());
    }

    @Test
    void testFindByStudentAndCourseAndStatus() {
        CoursePurchase purchase = new CoursePurchase();
        purchase.setStudent(student);
        purchase.setCourse(course);
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setPurchasePrice(new BigDecimal("49.99"));
        purchase.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(purchase);

        Optional<CoursePurchase> foundPurchase = coursePurchaseRepo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.PENDING);

        assertTrue(foundPurchase.isPresent());
        assertEquals(purchase.getId(), foundPurchase.get().getId());
    }
}
