package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.dto.PaymentRequestDto;
import com.github.ilim.backend.entity.AuditEntity;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PaymentIntentStatus;
import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.PendingPurchaseExistsException;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({CoursePurchaseService.class})
public class CoursePurchaseServiceTest {

    @Autowired
    private CoursePurchaseService coursePurchaseService;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private PaymentService paymentService;

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
    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        // Create student
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setBirthdate(LocalDate.now());
        student = userRepo.save(student);

        // Create instructor
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString());
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setBirthdate(LocalDate.now());
        instructor = userRepo.save(instructor);

        // Create course
        course = new Course();
        course.setTitle("Test Course");
        course.setPrice(new BigDecimal("49.99"));
        course.setInstructor(instructor);
        course = courseRepo.save(course);
    }

    @Test
    @Transactional
    void testPurchaseCourse_Success() {
        UUID courseId = course.getId();

        when(courseService.findPublishedCourse(courseId)).thenReturn(course);
        when(paymentService.createCheckoutSession(any(PaymentRequestDto.class))).thenReturn("http://payment.url");

        String paymentUrl = coursePurchaseService.purchaseCourse(student, courseId);

        assertNotNull(paymentUrl);
        assertEquals("http://payment.url", paymentUrl);

        List<CoursePurchase> purchases = coursePurchaseRepo.findAllByStudent(student, AuditEntity.SORT_BY_CREATED_AT_DESC);
        assertEquals(1, purchases.size());
        CoursePurchase purchase = purchases.get(0);
        assertEquals(PurchaseStatus.PENDING, purchase.getStatus());
    }

    @Test
    @Transactional
    void testPurchaseCourse_AlreadyPurchased() {
        UUID courseId = course.getId();

        CoursePurchase existingPurchase = new CoursePurchase();
        existingPurchase.setStudent(student);
        existingPurchase.setCourse(course);
        existingPurchase.setStatus(PurchaseStatus.SUCCEEDED);
        existingPurchase.setPurchasePrice(BigDecimal.TEN);
        existingPurchase.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(existingPurchase);

        when(courseService.findPublishedCourse(courseId)).thenReturn(course);

        assertThrows(AlreadyPurchasedCourseException.class, () -> {
            coursePurchaseService.purchaseCourse(student, courseId);
        });
    }

    @Test
    @Transactional
    void testPurchaseCourse_PendingPurchaseExists() {
        UUID courseId = course.getId();

        CoursePurchase existingPurchase = new CoursePurchase();
        existingPurchase.setStudent(student);
        existingPurchase.setCourse(course);
        existingPurchase.setStatus(PurchaseStatus.PENDING);
        existingPurchase.setPurchasePrice(BigDecimal.TEN);
        existingPurchase.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(existingPurchase);

        when(courseService.findPublishedCourse(courseId)).thenReturn(course);

        assertThrows(PendingPurchaseExistsException.class, () -> {
            coursePurchaseService.purchaseCourse(student, courseId);
        });
    }

    @Test
    void testPurchaseCourse_CantPurchaseOwnCourse() {
        UUID courseId = course.getId();

        when(courseService.findPublishedCourse(courseId)).thenReturn(course);

        assertThrows(CantPurchaseOwnCourseException.class, () -> {
            coursePurchaseService.purchaseCourse(instructor, courseId);
        });
    }

    @Test
    @Transactional
    void testConfirmPurchase() {
        PaymentEventDto paymentEventDto = new PaymentEventDto();
        paymentEventDto.setUserId(student.getId());
        paymentEventDto.setCourseId(course.getId().toString());
        paymentEventDto.setPaymentId("payment123");

        CoursePurchase pendingPurchase = new CoursePurchase();
        pendingPurchase.setStudent(student);
        pendingPurchase.setCourse(course);
        pendingPurchase.setStatus(PurchaseStatus.PENDING);
        pendingPurchase.setPurchasePrice(BigDecimal.TEN);
        pendingPurchase.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(pendingPurchase);

        when(userService.findById(student.getId())).thenReturn(student);
        when(courseService.findPublishedCourse(course.getId())).thenReturn(course);

        coursePurchaseService.confirmPurchase(paymentEventDto);

        CoursePurchase updatedPurchase = coursePurchaseRepo.findById(pendingPurchase.getId()).orElse(null);
        assertNotNull(updatedPurchase);
        assertEquals(PurchaseStatus.SUCCEEDED, updatedPurchase.getStatus());
        assertEquals("payment123", updatedPurchase.getPaymentId());
    }

    @Test
    @Transactional
    void testRejectPurchase() {
        PaymentEventDto paymentEventDto = new PaymentEventDto();
        paymentEventDto.setUserId(student.getId());
        paymentEventDto.setCourseId(course.getId().toString());
        paymentEventDto.setPaymentId("payment123");

        CoursePurchase pendingPurchase = new CoursePurchase();
        pendingPurchase.setStudent(student);
        pendingPurchase.setCourse(course);
        pendingPurchase.setStatus(PurchaseStatus.PENDING);
        pendingPurchase.setPurchasePrice(BigDecimal.TEN);
        pendingPurchase.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(pendingPurchase);

        when(userService.findById(student.getId())).thenReturn(student);
        when(courseService.findPublishedCourse(course.getId())).thenReturn(course);

        coursePurchaseService.rejectPurchase(paymentEventDto, PaymentIntentStatus.CANCELED);

        CoursePurchase updatedPurchase = coursePurchaseRepo.findById(pendingPurchase.getId()).orElse(null);
        assertNotNull(updatedPurchase);
        assertEquals(PurchaseStatus.FAILED, updatedPurchase.getStatus());
        assertEquals("payment123", updatedPurchase.getPaymentId());
        assertFalse(updatedPurchase.getFailedMessage().contains("FAILED"));
    }

    @Test
    @Transactional
    void testCancelCoursePurchase() {
        UUID courseId = course.getId();

        CoursePurchase pendingPurchase = new CoursePurchase();
        pendingPurchase.setStudent(student);
        pendingPurchase.setCourse(course);
        pendingPurchase.setStatus(PurchaseStatus.PENDING);
        pendingPurchase.setPurchasePrice(BigDecimal.TEN);
        pendingPurchase.setPurchaseDate(LocalDateTime.now());
        coursePurchaseRepo.save(pendingPurchase);

        when(courseService.findPublishedCourse(courseId)).thenReturn(course);

        coursePurchaseService.cancelCoursePurchase(student, courseId);

        CoursePurchase updatedPurchase = coursePurchaseRepo.findById(pendingPurchase.getId()).orElse(null);
        assertNotNull(updatedPurchase);
        assertEquals(PurchaseStatus.FAILED, updatedPurchase.getStatus());
        assertTrue(updatedPurchase.getFailedMessage().contains("cancelled by the student"));
    }
}
