package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.QuizDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.QuizService;
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

@WebMvcTest(QuizController.class)
@ActiveProfiles("test")
@Import({QuizController.class})
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User instructor;
    private User student;
    private UUID moduleId;
    private UUID quizId;
    private QuizDto quizDto;

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

        moduleId = UUID.randomUUID();
        quizId = UUID.randomUUID();

        quizDto = new QuizDto();
        quizDto.setTitle("Sample Quiz");
        quizDto.setPassingScore(new BigDecimal("70"));
    }

    @Test
    void testFindQuizById() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(quizService.getQuizDtoByQuizId(student, quizId)).thenReturn(quizDto);

        mockMvc.perform(get("/quiz/" + quizId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.title").value("Sample Quiz"))
            .andExpect(jsonPath("$.body.passingScore").value(70));

        verify(quizService, times(1)).getQuizDtoByQuizId(student, quizId);
    }

    @Test
    void testAddQuizToModule() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(post("/instructor/module/" + moduleId + "/add-quiz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isBadRequest());

        quizService.addQuizToModule(instructor, moduleId, quizDto);
    }

    @Test
    void testUpdateQuiz() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        quizDto.setTitle("Updated Quiz Title");

        mockMvc.perform(put("/instructor/update-quiz/" + quizId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quizDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isBadRequest());

        quizService.updateQuiz(instructor, quizId, quizDto);
    }

    @Test
    void testRemoveQuizFromModule() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(delete("/instructor/delete-quiz/" + quizId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Quiz removed successfully from the module"));

        verify(quizService, times(1)).removeQuizFromModule(instructor, quizId);
    }
}
