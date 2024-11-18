package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.CertificateService;
import com.github.ilim.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CertificateController.class)
@ActiveProfiles("test")
@Import({CertificateController.class})
public class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService certificateService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;

    @BeforeEach
    void setUp() {
        // Create student user
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student User");
    }

    @Test
    void testCheckCourseProgress() throws Exception {
        UUID courseId = UUID.randomUUID();
        CourseProgressDto progressDto = new CourseProgressDto(courseId, 5, 5);

        when(userService.findById(student.getId())).thenReturn(student);
        when(certificateService.checkCourseProgress(student, courseId)).thenReturn(progressDto);

        mockMvc.perform(get("/student/course/" + courseId + "/check-progress")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.totalQuizzes").value(5))
            .andExpect(jsonPath("$.body.completedQuizzes").value(5));

        verify(certificateService, times(1)).checkCourseProgress(student, courseId);
    }

    @Test
    void testGenerateCertificate() throws Exception {
        UUID courseId = UUID.randomUUID();
        byte[] pdfBytes = "PDF content".getBytes();

        when(userService.findById(student.getId())).thenReturn(student);
        when(certificateService.generatePdfCertificate(student, courseId)).thenReturn(pdfBytes);

        mockMvc.perform(post("/student/course/" + courseId + "/certificate")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"certificate.pdf\""))
            .andExpect(content().bytes(pdfBytes));

        verify(certificateService, times(1)).generatePdfCertificate(student, courseId);
    }
}
