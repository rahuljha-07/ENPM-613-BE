package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.StudentService;
import com.github.ilim.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@ActiveProfiles("test")
@Import({StudentController.class})
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private UUID courseId;
    private CourseProgressDto progressDto;

    @BeforeEach
    void setUp() {
        // Create student user
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student User");
        student.setRole(UserRole.STUDENT);

        courseId = UUID.randomUUID();
        progressDto = new CourseProgressDto(courseId, 5, 3);
    }

    @Test
    void testGetStudentQuizProgress() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(studentService.getCourseQuizProgress(student, courseId)).thenReturn(progressDto);

        mockMvc.perform(get("/student/" + courseId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.courseId").value(courseId.toString()));

        verify(studentService, times(1)).getCourseQuizProgress(student, courseId);
    }
}
