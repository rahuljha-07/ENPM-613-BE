package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.InstructorAppService;
import com.github.ilim.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorAppController.class)
@ActiveProfiles("test")
@Import({InstructorAppController.class})
public class InstructorAppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorAppService appService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User studentUser;
    private User adminUser;
    private InstructorApp instructorApp;
    private InstructorAppDto appDto;

    @BeforeEach
    void setUp() {
        // Create student user
        studentUser = new User();
        studentUser.setId("student123");
        studentUser.setEmail("student@example.com");
        studentUser.setName("Student User");
        studentUser.setRole(UserRole.STUDENT);

        // Create admin user
        adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);

        // Create an instructor application
        instructorApp = new InstructorApp();
        instructorApp.setId(UUID.randomUUID());
        instructorApp.setUserId(studentUser.getId());
        instructorApp.setStatus(ApplicationStatus.PENDING);
        instructorApp.setInstructorBio("Experienced Teacher");
        instructorApp.setInstructorTitle("Senior Developer");

        // Create an InstructorAppDto
        appDto = new InstructorAppDto();
        appDto.setInstructorBio("Experienced Teacher");
        appDto.setInstructorTitle("Senior Developer");
        appDto.setProfileImageUrl("http://example.com/profile.jpg");
    }

    @Test
    void testGetCurrentUserInstructorApp() throws Exception {
        when(userService.findById(studentUser.getId())).thenReturn(studentUser);
        when(appService.findByUserId(studentUser.getId(), null)).thenReturn(List.of(instructorApp));

        mockMvc.perform(get("/student/instructor-application")
                .with(jwt().jwt(jwt -> jwt.claim("sub", studentUser.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body[0].userId").value(studentUser.getId()))
            .andExpect(jsonPath("$.body[0].status").value("PENDING"))
            .andExpect(jsonPath("$.body[0].instructorBio").value("Experienced Teacher"));

        verify(appService, times(1)).findByUserId(studentUser.getId(), null);
    }

    @Test
    void testSubmitInstructorApp() throws Exception {
        when(userService.findById(studentUser.getId())).thenReturn(studentUser);

        mockMvc.perform(post("/student/instructor-application/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", studentUser.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.body").value("Application submitted successfully"));

        verify(appService, times(1)).saveInstructorApp(studentUser, appDto);
    }

    @Test
    void testCancelPendingInstructorApplication() throws Exception {
        when(userService.findById(studentUser.getId())).thenReturn(studentUser);

        mockMvc.perform(post("/student/instructor-application/cancel")
                .with(jwt().jwt(jwt -> jwt.claim("sub", studentUser.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.body").value("Pending Instructor Application has been canceled successfully"));

        verify(appService, times(1)).cancelPendingInstructorApplication(studentUser);
    }

    @Test
    void testGetAllApplicationsAsAdmin() throws Exception {
        when(appService.findAll()).thenReturn(List.of(instructorApp));

        mockMvc.perform(get("/admin/instructor-application/all")
                .with(jwt().jwt(jwt -> jwt.claim("sub", adminUser.getId())).authorities(() -> "ROLE_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body[0].userId").value(studentUser.getId()))
            .andExpect(jsonPath("$.body[0].status").value("PENDING"));

        verify(appService, times(1)).findAll();
    }

    @Test
    void testGetAllPendingApplicationsAsAdmin() throws Exception {
        when(appService.findPendingApplications()).thenReturn(List.of(instructorApp));

        mockMvc.perform(get("/admin/instructor-application/all-pending")
                .with(jwt().jwt(jwt -> jwt.claim("sub", adminUser.getId())).authorities(() -> "ROLE_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body[0].userId").value(studentUser.getId()))
            .andExpect(jsonPath("$.body[0].status").value("PENDING"));

        verify(appService, times(1)).findPendingApplications();
    }
}
