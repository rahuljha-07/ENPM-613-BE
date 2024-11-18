package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.CantGenerateCertificateException;
import com.github.ilim.backend.exception.exceptions.StudentDidNotCompleteCourseException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({CertificateService.class})
public class CertificateServiceTest {

    @Autowired
    private CertificateService certificateService;

    @MockBean
    private TemplateEngine templateEngine;

    @MockBean
    private CourseService courseService;

    @MockBean
    private StudentService studentService;

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
        // Create student
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student = userRepo.save(student);

        // Create course
        course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Test Course");
        course = courseRepo.save(course);
    }

    @Test
    void testGeneratePdfCertificate_Success() throws Exception {
        UUID courseId = course.getId();

        when(courseService.findCourseByIdAndUser(student, courseId)).thenReturn(course);
        when(studentService.getCourseQuizProgress(student, courseId))
            .thenReturn(new CourseProgressDto(courseId, 5, 5));
        when(templateEngine.process(anyString(), any())).thenReturn("<html></html>");

        byte[] pdfBytes = certificateService.generatePdfCertificate(student, courseId);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        verify(templateEngine).process(anyString(), any());
    }

    @Test
    void testGeneratePdfCertificate_NotCompleted() {
        UUID courseId = course.getId();

        when(courseService.findCourseByIdAndUser(student, courseId)).thenReturn(course);
        when(studentService.getCourseQuizProgress(student, courseId))
            .thenReturn(new CourseProgressDto(courseId, 3, 5));

        assertThrows(StudentDidNotCompleteCourseException.class, () -> {
            certificateService.generatePdfCertificate(student, courseId);
        });
    }

    @Test
    void testGeneratePdfCertificate_GenerationException() throws Exception {
        UUID courseId = course.getId();

        when(courseService.findCourseByIdAndUser(student, courseId)).thenReturn(course);
        when(studentService.getCourseQuizProgress(student, courseId))
            .thenReturn(new CourseProgressDto(courseId, 5, 5));
        when(templateEngine.process(anyString(), any())).thenThrow(new RuntimeException("Template error"));

        assertThrows(CantGenerateCertificateException.class, () -> {
            certificateService.generatePdfCertificate(student, courseId);
        });
    }

    @Test
    void testCheckCourseProgress() {
        UUID courseId = course.getId();

        CourseProgressDto expectedProgress = new CourseProgressDto(courseId, 4, 5);
        when(courseService.findCourseByIdAndUser(student, courseId)).thenReturn(course);
        when(studentService.getCourseQuizProgress(student, courseId)).thenReturn(expectedProgress);

        CourseProgressDto progressDto = certificateService.checkCourseProgress(student, courseId);

        assertNotNull(progressDto);
        assertEquals(expectedProgress, progressDto);
    }
}
