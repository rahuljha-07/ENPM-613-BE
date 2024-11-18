package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.QuizAttemptDto;
import com.github.ilim.backend.dto.QuizAttemptResultDto;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.QuizAttemptService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizAttemptController.class)
@ActiveProfiles("test")
@Import({QuizAttemptController.class})
public class QuizAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizAttemptService quizAttemptService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User student;
    private User instructor;
    private UUID quizId;
    private QuizAttemptDto attemptDto;
    private QuizAttemptResultDto resultDto;
    private QuizAttempt quizAttempt;

    @BeforeEach
    void setUp() {
        // Create student user
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student User");
        student.setRole(UserRole.STUDENT);

        // Create instructor user
        instructor = new User();
        instructor.setId("instructor123");
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor User");
        instructor.setRole(UserRole.INSTRUCTOR);

        quizId = UUID.randomUUID();
        var quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Quiz Title");
        quiz.setDescription("Quiz Description");
        quiz.setPassingScore(BigDecimal.TEN);
        quiz.setQuestions(List.of());

        attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(quizId);
        attemptDto.setAnswers(List.of());

        resultDto = new QuizAttemptResultDto();
        resultDto.setAttemptId(UUID.randomUUID());
        resultDto.setPassed(true);
        resultDto.setUserScore(BigDecimal.TEN);
        resultDto.setTotalScore(BigDecimal.TEN);

        quizAttempt = new QuizAttempt();
        quizAttempt.setId(UUID.randomUUID());
        quizAttempt.setQuiz(quiz);
        quizAttempt.setStudent(student);
    }

    @Test
    void testSubmitQuizAttempt() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(quizAttemptService.processQuizAttempt(any(User.class), any(QuizAttemptDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/student/attempt-quiz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attemptDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.passed").value(true))
            .andExpect(jsonPath("$.body.userScore").value(10));

        verify(quizAttemptService, times(1)).processQuizAttempt(student, attemptDto);
    }

    @Test
    void testGetAllQuizAttemptsForStudent() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(quizAttemptService.getAllStudentQuizAttempts(student, quizId)).thenReturn(List.of(resultDto));

        mockMvc.perform(get("/student/quiz-attempt/" + quizId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body", hasSize(1)))
            .andExpect(jsonPath("$.body[0].passed").value(true));

        verify(quizAttemptService, times(1)).getAllStudentQuizAttempts(student, quizId);
    }

    @Test
    void testGetLastQuizAttemptForStudent() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(quizAttemptService.getLastStudentQuizAttempt(student, quizId)).thenReturn(resultDto);

        mockMvc.perform(get("/student/quiz-attempt/" + quizId + "/last")
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId())).authorities(() -> "ROLE_STUDENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.passed").value(true));

        verify(quizAttemptService, times(1)).getLastStudentQuizAttempt(student, quizId);
    }

    @Test
    void testGetAllQuizAttemptsForInstructor() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);
        when(quizAttemptService.getAllQuizAttemptsForQuiz(instructor, quizId)).thenReturn(List.of(quizAttempt));

        mockMvc.perform(get("/instructor/quiz-attempt/" + quizId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body", hasSize(1)))
            .andExpect(jsonPath("$.body[0].studentId").value(student.getId()));

        verify(quizAttemptService, times(1)).getAllQuizAttemptsForQuiz(instructor, quizId);
    }
}
