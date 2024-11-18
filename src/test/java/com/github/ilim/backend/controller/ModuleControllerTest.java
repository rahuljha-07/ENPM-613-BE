package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.ModuleDto;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.ModuleService;
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

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModuleController.class)
@ActiveProfiles("test")
@Import({ModuleController.class})
public class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleService moduleService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User instructor;
    private User student;
    private UUID courseId;
    private UUID moduleId;
    private ModuleDto moduleDto;
    private CourseModule module;

    @BeforeEach
    void setUp() {
        // Create instructor user
        instructor = new User();
        instructor.setId("instructor123");
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor User");
        instructor.setRole(UserRole.INSTRUCTOR);

        // Create student user
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student User");
        student.setRole(UserRole.STUDENT);

        courseId = UUID.randomUUID();
        moduleId = UUID.randomUUID();

        moduleDto = new ModuleDto();
        moduleDto.setTitle("Module 1");
        moduleDto.setDescription("Module Description");

        module = new CourseModule();
        module.setId(moduleId);
        module.setTitle("Module 1");
        module.setDescription("Module Description");
    }

    @Test
    void testFindModuleByIdAsStudent() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(moduleService.findCourseModuleById(student, moduleId)).thenReturn(module);

        mockMvc.perform(get("/module/" + moduleId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().is5xxServerError());

        verify(moduleService, times(1)).findCourseModuleById(student, moduleId);
    }

    @Test
    void testAddModuleToCourse() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(post("/instructor/course/" + courseId + "/add-module")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moduleDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.body").value("Module added successfully to course."));

        verify(moduleService, times(1)).addModuleToCourse(instructor, courseId, moduleDto);
    }

    @Test
    void testUpdateModule() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        moduleDto.setTitle("Updated Module Title");

        mockMvc.perform(put("/instructor/update-module/" + moduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moduleDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Module updated successfully."));

        verify(moduleService, times(1)).updateCourseModule(instructor, moduleId, moduleDto);
    }

    @Test
    void testDeleteCourseModule() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(delete("/instructor/delete-module/" + moduleId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Module deleted successfully."));

        verify(moduleService, times(1)).deleteCourseModule(instructor, moduleId);
    }
}
