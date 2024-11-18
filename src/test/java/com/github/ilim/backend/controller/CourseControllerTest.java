package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.dto.PublicCourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.CoursePurchaseService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@ActiveProfiles("test")
@Import({CourseController.class})
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoursePurchaseService coursePurchaseService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User student;
    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        // Create student user
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student User");
        student.setRole(UserRole.STUDENT);

        // Create instructor user
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString());
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor User");
        instructor.setRole(UserRole.INSTRUCTOR);

        // Create a course
        course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Test Course");
        course.setPrice(BigDecimal.valueOf(99.99));
        course.setInstructor(instructor);
    }

    @Test
    void testFilterPublishedCourses() throws Exception {
        PublicCourseDto courseDto = new PublicCourseDto();
        courseDto.setId(UUID.randomUUID());
        courseDto.setTitle("Published Course");

        when(courseService.findPublishedCourses()).thenReturn(List.of(courseDto));

        mockMvc.perform(get("/course/published")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT"))
            )
            .andExpect(status().isOk())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"));

        verify(courseService, times(1)).filterPublishedCourses(null);
    }

    @Test
    void testFindCourseById() throws Exception {
        when(courseService.findCourseByIdAndUser(null, course.getId())).thenReturn(course);

        mockMvc.perform(get("/course/" + course.getId())
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT"))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"));

        verify(courseService, times(1)).findCourseByIdAndUser(null, course.getId());
    }

    @Test
    void testFindPurchasedCourses() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(courseService.findPurchasedCourses(student)).thenReturn(List.of(course));

        mockMvc.perform(get("/student/course/purchased")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body[0].title").value("Test Course"));

        verify(courseService, times(1)).findPurchasedCourses(student);
    }

    @Test
    void testFindCreatedCourses() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);
        when(courseService.findCreatedCourses(instructor)).thenReturn(List.of(course));

        mockMvc.perform(get("/instructor/course/created")
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body[0].title").value("Test Course"));

        verify(courseService, times(1)).findCreatedCourses(instructor);
    }

    @Test
    void testCreateCourse() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("New Course");
        courseDto.setPrice(BigDecimal.valueOf(49.99));

        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(post("/instructor/create-course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isBadRequest());

        courseService.create(instructor, courseDto);
    }

    @Test
    void testUpdateCourse() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Updated Course Title");
        courseDto.setPrice(BigDecimal.valueOf(59.99));

        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(put("/instructor/update-course/" + course.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isBadRequest());

        courseService.updateCourse(instructor, course.getId(), courseDto);
    }

    @Test
    void testSubmitCourseForApproval() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(post("/instructor/course/" + course.getId() + "/submit-for-approval")
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Course submitted successfully to the admin to review it."));

        verify(courseService, times(1)).submitCourseForApproval(instructor, course.getId());
    }

    @Test
    void testPurchaseCourse() throws Exception {
        String checkoutUrl = "http://checkout.url";
        when(userService.findById(student.getId())).thenReturn(student);
        when(coursePurchaseService.purchaseCourse(student, course.getId())).thenReturn(checkoutUrl);

        mockMvc.perform(post("/student/purchase-course/" + course.getId())
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value(checkoutUrl));

        verify(coursePurchaseService, times(1)).purchaseCourse(student, course.getId());
    }

    @Test
    void testReorderCourseModules() throws Exception {
        List<UUID> modulesOrder = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(put("/instructor/course/" + course.getId() + "/reorder-modules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modulesOrder))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Course modules reordered successfully."));

        verify(courseService, times(1)).reorderCourseModules(instructor, course.getId(), modulesOrder);
    }

    @Test
    void testCheckCoursePurchase() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(coursePurchaseService.checkCoursePurchase(student, course.getId())).thenReturn(PurchaseStatus.SUCCEEDED);

        mockMvc.perform(post("/student/course/" + course.getId() + "/check-purchase")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("SUCCEEDED"));

        verify(coursePurchaseService, times(1)).checkCoursePurchase(student, course.getId());
    }

    @Test
    void testCancelCoursePurchase() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);

        mockMvc.perform(post("/student/course/" + course.getId() + "/cancel-purchase")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("All pending requests has been canceled"));

        verify(coursePurchaseService, times(1)).cancelCoursePurchase(student, course.getId());
    }

    @Test
    void testSimulateCoursePurchaseConfirmation() throws Exception {
        PaymentEventDto dto = new PaymentEventDto();
        dto.setUserId(student.getId());
        dto.setCourseId(course.getId().toString());
        dto.setPaymentId("payment123");

        mockMvc.perform(post("/admin/confirm-course-purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(jwt().authorities(() -> "ROLE_ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"));

        verify(coursePurchaseService, times(1)).simulateCoursePurchaseConfirmation(dto);
    }
}
