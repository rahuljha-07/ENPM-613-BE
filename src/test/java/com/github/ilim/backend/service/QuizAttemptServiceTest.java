package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuizAttemptDto;
import com.github.ilim.backend.dto.QuizAttemptResultDto;
import com.github.ilim.backend.dto.UserAnswerDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.QuestionOption;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.QuestionType;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCantAttemptQuizzesException;
import com.github.ilim.backend.exception.exceptions.CantAttemptOwnQuizException;
import com.github.ilim.backend.exception.exceptions.MissingAnswerException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.QuizAttemptsNotFoundException;
import com.github.ilim.backend.exception.exceptions.QuizNotFoundException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.repo.ModuleRepo;
import com.github.ilim.backend.repo.QuestionOptionRepo;
import com.github.ilim.backend.repo.QuestionRepo;
import com.github.ilim.backend.repo.QuizAttemptRepo;
import com.github.ilim.backend.repo.QuizRepo;
import com.github.ilim.backend.repo.UserAnswerOptionRepo;
import com.github.ilim.backend.repo.UserAnswerRepo;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuizAttemptService.class})
public class QuizAttemptServiceTest {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private QuizAttemptRepo quizAttemptRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private ModuleRepo moduleRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private QuestionOptionRepo questionOptionRepo;

    @Autowired
    private UserAnswerRepo userAnswerRepo;

    @Autowired
    private UserAnswerOptionRepo userAnswerOptionRepo;

    @MockBean
    private QuizService quizService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private User instructor;
    private Course course;
    private CourseModule module;
    private Quiz quiz;
    private Question question;
    private QuestionOption correctOption;
    private QuestionOption incorrectOption;

    @BeforeEach
    void setUp() {
        // Create student
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setRole(UserRole.STUDENT);
        student.setBirthdate(LocalDateTime.now().toLocalDate());
        student = userRepo.save(student);

        // Create instructor
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString());
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setBirthdate(LocalDateTime.now().toLocalDate());
        instructor = userRepo.save(instructor);

        // Create a course
        course = new Course();
        course.setTitle("Test Course");
        course.setPrice(new BigDecimal("49.99"));
        course.setStatus(CourseStatus.DRAFT);
        course.setInstructor(instructor);
        course = courseRepo.save(course);

        module = new CourseModule();
        module.setId(UUID.randomUUID());
        module.setCourse(course);
        module.setTitle("title");
        module.setOrderIndex(0);
        module.setDescription("description");
        module = moduleRepo.save(module);

        // Create quiz
        quiz = new Quiz();
        quiz.setId(UUID.randomUUID());
        quiz.setTitle("Test Quiz");
        quiz.setPassingScore(new BigDecimal("70"));
        quiz.setCourseModule(module);
        quiz = quizRepo.save(quiz);

        // Create question
        question = new Question();
        question.setId(UUID.randomUUID());
        question.setText("What is 2 + 2?");
        question.setPoints(new BigDecimal("10"));
        question.setType(QuestionType.TRUE_FALSE);
        question.setQuiz(quiz);
        question = questionRepo.save(question);

        quiz.setQuestions(List.of(question));

        // Create correct option
        correctOption = new QuestionOption();
        correctOption.setId(UUID.randomUUID());
        correctOption.setText("4");
        correctOption.setIsCorrect(true);
        correctOption.setQuestion(question);
        correctOption = questionOptionRepo.save(correctOption);

        // Create incorrect option
        incorrectOption = new QuestionOption();
        incorrectOption.setId(UUID.randomUUID());
        incorrectOption.setText("5");
        incorrectOption.setIsCorrect(false);
        incorrectOption.setQuestion(question);
        incorrectOption = questionOptionRepo.save(incorrectOption);
    }

    @Test
    @Transactional
    void testProcessQuizAttempt_Success_Passed() {
        QuizAttemptDto attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(quiz.getId());
        UserAnswerDto answerDto = new UserAnswerDto();
        answerDto.setQuestionId(question.getId());
        answerDto.setSelectedOptionIds(List.of(correctOption.getId()));
        attemptDto.setAnswers(List.of(answerDto));

        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        QuizAttemptResultDto resultDto = quizAttemptService.processQuizAttempt(student, attemptDto);

        assertNotNull(resultDto);
        assertFalse(resultDto.isPassed());
        assertEquals(new BigDecimal("10"), resultDto.getUserScore());
        assertEquals(new BigDecimal("10"), resultDto.getTotalScore());
    }

    @Test
    @Transactional
    void testProcessQuizAttempt_Success_Failed() {
        QuizAttemptDto attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(quiz.getId());
        UserAnswerDto answerDto = new UserAnswerDto();
        answerDto.setQuestionId(question.getId());
        answerDto.setSelectedOptionIds(List.of(incorrectOption.getId()));
        attemptDto.setAnswers(List.of(answerDto));

        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        QuizAttemptResultDto resultDto = quizAttemptService.processQuizAttempt(student, attemptDto);

        assertNotNull(resultDto);
        assertFalse(resultDto.isPassed());
        assertEquals(BigDecimal.ZERO, resultDto.getUserScore());
        assertEquals(new BigDecimal("10"), resultDto.getTotalScore());
    }

    @Test
    void testProcessQuizAttempt_AdminUser() {
        student.setRole(UserRole.ADMIN);
        student = userRepo.save(student);

        QuizAttemptDto attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(quiz.getId());
        attemptDto.setAnswers(List.of());

        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        assertThrows(AdminCantAttemptQuizzesException.class, () -> {
            quizAttemptService.processQuizAttempt(student, attemptDto);
        });
    }

    @Test
    void testProcessQuizAttempt_AttemptOwnQuiz() {
        when(quizService.findQuizById(instructor, quiz.getId())).thenReturn(quiz);

        QuizAttemptDto attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(quiz.getId());
        attemptDto.setAnswers(List.of());

        assertThrows(CantAttemptOwnQuizException.class, () -> {
            quizAttemptService.processQuizAttempt(instructor, attemptDto);
        });
    }

    @Test
    @Transactional
    void testGetAllStudentQuizAttempts_Success() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(UUID.randomUUID());
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setPassed(true);
        attempt.setUserScore(new BigDecimal("10"));
        attempt.setTotalScore(new BigDecimal("10"));
        quizAttemptRepo.save(attempt);

        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        List<QuizAttemptResultDto> attempts = quizAttemptService.getAllStudentQuizAttempts(student, quiz.getId());

        assertNotNull(attempts);
        assertEquals(1, attempts.size());
    }

    @Test
    void testGetAllStudentQuizAttempts_NotFound() {
        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        assertThrows(QuizAttemptsNotFoundException.class, () -> {
            quizAttemptService.getAllStudentQuizAttempts(student, quiz.getId());
        });
    }
    @Test
    void testGetLastStudentQuizAttempt_Success() {
        QuizAttempt attempt1 = new QuizAttempt();
        attempt1.setQuiz(quiz);
        attempt1.setStudent(student);
        attempt1.setCreatedAt(LocalDateTime.now().minusHours(2));
        quizAttemptRepo.save(attempt1);

        QuizAttempt attempt2 = new QuizAttempt();
        attempt2.setQuiz(quiz);
        attempt2.setStudent(student);
        attempt2.setCreatedAt(LocalDateTime.now().minusHours(1));
        quizAttemptRepo.save(attempt2);

        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        QuizAttemptResultDto lastAttempt = quizAttemptService.getLastStudentQuizAttempt(student, quiz.getId());

        assertNotNull(lastAttempt);
        assertEquals(attempt2.getId(), lastAttempt.getAttemptId());
    }

    @Test
    void testGetLastStudentQuizAttempt_NotFound() {
        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        assertThrows(QuizAttemptsNotFoundException.class, () -> {
            quizAttemptService.getLastStudentQuizAttempt(student, quiz.getId());
        });
    }

    @Test
    void testGetAllQuizAttemptsForQuiz_AsInstructor() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        quizAttemptRepo.save(attempt);

        when(quizService.findQuizByIdAsInstructor(instructor, quiz.getId())).thenReturn(quiz);

        List<QuizAttempt> attempts = quizAttemptService.getAllQuizAttemptsForQuiz(instructor, quiz.getId());

        assertNotNull(attempts);
        assertEquals(0, attempts.size());
    }

    @Test
    void testGetAllQuizAttemptsForQuiz_AsNonInstructor() {
        when(quizService.findQuizByIdAsInstructor(student, quiz.getId()))
            .thenThrow(new NotCourseInstructorException(student, quiz));

        quizAttemptService.getAllQuizAttemptsForQuiz(student, quiz.getId());
    }
    @Test
    void testProcessQuizAttempt_QuizNotFound() {
        UUID nonExistentQuizId = UUID.randomUUID();

        QuizAttemptDto attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(nonExistentQuizId);
        attemptDto.setAnswers(List.of());

        when(quizService.findQuizById(student, nonExistentQuizId))
            .thenThrow(new QuizNotFoundException(nonExistentQuizId));

        assertThrows(QuizNotFoundException.class, () -> {
            quizAttemptService.processQuizAttempt(student, attemptDto);
        });
    }

    @Test
    void testProcessQuizAttempt_InvalidAnswers() {
        QuizAttemptDto attemptDto = new QuizAttemptDto();
        attemptDto.setQuizId(quiz.getId());
        UserAnswerDto answerDto = new UserAnswerDto();
        answerDto.setQuestionId(UUID.randomUUID());
        answerDto.setSelectedOptionIds(List.of());
        attemptDto.setAnswers(List.of(answerDto));

        when(quizService.findQuizById(student, quiz.getId())).thenReturn(quiz);

        assertThrows(MissingAnswerException.class, () -> {
            quizAttemptService.processQuizAttempt(student, attemptDto);
        });
    }

}
