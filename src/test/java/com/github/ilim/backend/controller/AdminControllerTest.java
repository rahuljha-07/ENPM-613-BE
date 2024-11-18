package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.CourseRejectionDto;
import com.github.ilim.backend.dto.RespondToInstructorAppDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.AdminService;
import com.github.ilim.backend.service.CourseService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
@Import({AdminController.class})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User studentUser;
    private User instructorUser;
    private Course course;

    @BeforeEach
    void setUp() {
        // Create admin user
        adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);

        // Create student user
        studentUser = new User();
        studentUser.setId("student123");
        studentUser.setEmail("student@example.com");
        studentUser.setName("Student User");
        studentUser.setRole(UserRole.STUDENT);

        // Create instructor user
        instructorUser = new User();
        instructorUser.setId("instructor123");
        instructorUser.setEmail("instructor@example.com");
        instructorUser.setName("Instructor User");
        instructorUser.setRole(UserRole.INSTRUCTOR);

        // Create a course
        course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Test Course");
        course.setStatus(CourseStatus.WAIT_APPROVAL);
    }

    @Test
    void testApproveInstructorApplication() throws Exception {
        RespondToInstructorAppDto dto = new RespondToInstructorAppDto();
        dto.setInstructorApplicationId(UUID.randomUUID());

        mockMvc.perform(post("/admin/approve-instructor-application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Application approved"));

        verify(adminService, times(1)).approveInstructorApp(dto.getInstructorApplicationId());
    }

    @Test
    void testRejectInstructorApplication() throws Exception {
        RespondToInstructorAppDto dto = new RespondToInstructorAppDto();
        dto.setInstructorApplicationId(UUID.randomUUID());
        dto.setMessage("Insufficient qualifications");

        mockMvc.perform(post("/admin/reject-instructor-application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Application rejected"));

        verify(adminService, times(1)).rejectInstructorApp(dto.getInstructorApplicationId(), dto.getMessage());
    }

    @Test
    void testBlockUser() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);

        mockMvc.perform(post("/admin/block-user/" + studentUser.getId())
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("User has been blocked successfully"));

        verify(adminService, times(1)).blockUser(adminUser, studentUser.getId());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of(adminUser, instructorUser, studentUser));

        mockMvc.perform(get("/admin/user/all")
                .with(jwt().authorities(() -> "ROLE_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body", hasSize(3)));

        verify(userService, times(1)).getAll();
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);
        when(userService.findByIdAsAdmin(adminUser, studentUser.getId())).thenReturn(studentUser);

        mockMvc.perform(get("/admin/user/" + studentUser.getId())
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.id").value(studentUser.getId()))
            .andExpect(jsonPath("$.body.email").value(studentUser.getEmail()));

        verify(userService, times(1)).findByIdAsAdmin(adminUser, studentUser.getId());
    }

    @Test
    void testDeleteCourseAsAdmin() throws Exception {
        mockMvc.perform(delete("/admin/delete-course/" + course.getId())
                .with(jwt().authorities(() -> "ROLE_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Course deleted successfully"));

        verify(courseService, times(1)).deleteCourseAsAdmin(course.getId());
    }

    @Test
    void testApproveCourse() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);

        mockMvc.perform(post("/admin/approve-course/" + course.getId())
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Course Approved successfully"));

        verify(courseService, times(1)).approveCourse(adminUser, course.getId());
    }

    @Test
    void testRejectCourse() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);

        CourseRejectionDto dto = new CourseRejectionDto();
        dto.setReason("Content not sufficient");

        mockMvc.perform(post("/admin/reject-course/" + course.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Course Rejected successfully"));

        verify(courseService, times(1)).rejectCourse(adminUser, course.getId(), dto);
    }

    @Test
    void testFindAllCourses() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);
        when(courseService.findAllCourses(adminUser)).thenReturn(List.of(course));

        mockMvc.perform(get("/admin/course/all")
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().is5xxServerError());

        verify(courseService, times(1)).findAllCourses(adminUser);
    }

    @Test
    void testFindCoursesWaitingForApproval() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);
        when(courseService.findCoursesWaitingForApproval(adminUser)).thenReturn(List.of(course));

        mockMvc.perform(get("/admin/course/wait-for-approval")
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().is5xxServerError());

        verify(courseService, times(1)).findCoursesWaitingForApproval(adminUser);
    }

    @Test
    void testTestingDemoteInstructor() throws Exception {
        when(userService.findById(adminUser.getId())).thenReturn(adminUser);

        mockMvc.perform(post("/admin/integration-tests/demote-instructor/" + instructorUser.getId())
                .with(jwt().authorities(() -> "ROLE_ADMIN").jwt(jwt -> jwt.claim("sub", adminUser.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("[testing only] Instructor became student again"));

        verify(courseService, times(1)).testingDemoteInstructor(adminUser, instructorUser.getId());
    }
}
