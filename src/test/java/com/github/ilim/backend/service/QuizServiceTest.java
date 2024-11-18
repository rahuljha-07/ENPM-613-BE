package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.QuestionDto;
import com.github.ilim.backend.dto.QuestionOptionDto;
import com.github.ilim.backend.dto.QuizDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.QuestionType;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CantDeleteAttemptedQuizException;
import com.github.ilim.backend.exception.exceptions.QuizNotFoundException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.repo.ModuleItemRepo;
import com.github.ilim.backend.repo.ModuleRepo;
import com.github.ilim.backend.repo.QuestionOptionRepo;
import com.github.ilim.backend.repo.QuestionRepo;
import com.github.ilim.backend.repo.QuizAttemptRepo;
import com.github.ilim.backend.repo.QuizRepo;
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
@Import({QuizService.class})
public class QuizServiceTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private QuestionOptionRepo questionOptionRepo;

    @Autowired
    private QuizAttemptRepo quizAttemptRepo;

    @Autowired
    private ModuleItemRepo moduleItemRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModuleRepo moduleRepo;

    // Mock dependencies
    @MockBean
    private CourseService courseService;

    @MockBean
    private ModuleService moduleService;

    @Autowired
    private CourseRepo courseRepo;

    @MockBean
    private ModuleItemService moduleItemService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User instructor;
    private User student;
    private Course course;
    private CourseModule module;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        // Create an instructor
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

        // Create a module
        module = new CourseModule();
        module.setId(UUID.randomUUID());
        module.setTitle("Module 1");
        module.setCourse(course);
        module = moduleRepo.save(module);

        // Create a quiz
        quiz = new Quiz();
        quiz.setId(UUID.randomUUID());
        quiz.setTitle("Test Quiz");
        quiz.setCourseModule(module);
        quiz.setPassingScore(BigDecimal.TEN);
        quiz = quizRepo.save(quiz);
    }

    @Test
    @Transactional
    void testAddQuizToModule_Success() {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("New Quiz");
        quizDto.setPassingScore(new BigDecimal("70"));

        // Add questions and options
        QuestionDto questionDto = new QuestionDto();
        questionDto.setText("What is 2 + 2?");
        questionDto.setPoints(new BigDecimal("10"));
        questionDto.setType(QuestionType.TRUE_FALSE);

        QuestionOptionDto correctOptionDto = new QuestionOptionDto();
        correctOptionDto.setText("4");
        correctOptionDto.setIsCorrect(true);

        QuestionOptionDto incorrectOptionDto = new QuestionOptionDto();
        incorrectOptionDto.setText("5");
        incorrectOptionDto.setIsCorrect(false);

        questionDto.setOptions(List.of(correctOptionDto, incorrectOptionDto));
        quizDto.setQuestions(List.of(questionDto));

        when(moduleService.findModuleByIdAsInstructor(instructor, module.getId())).thenReturn(module);

        quizService.addQuizToModule(instructor, module.getId(), quizDto);

        List<Quiz> quizzes = quizRepo.findAll();
        assertEquals(2, quizzes.size()); // Including the initial quiz
        Quiz addedQuiz = quizzes.stream().filter(q -> q.getTitle().equals("New Quiz")).findFirst().orElse(null);
        assertNotNull(addedQuiz);
        assertEquals("New Quiz", addedQuiz.getTitle());
        assertEquals(module.getId(), addedQuiz.getCourseModule().getId());
    }

    @Test
    void testFindQuizById_NotFound() {
        UUID nonExistentQuizId = UUID.randomUUID();
        quizRepo.findById(nonExistentQuizId);

        assertThrows(QuizNotFoundException.class, () -> {
            quizService.findQuizById(instructor, nonExistentQuizId);
        });
    }

    @Test
    void testRemoveQuizFromModule_Success() {
        quizService.findQuizByIdAsInstructor(instructor, quiz.getId());

        CourseModuleItem moduleItem = new CourseModuleItem();
        moduleItem.setId(UUID.randomUUID());
        moduleItem.setQuiz(quiz);
        moduleItem = moduleItemRepo.save(moduleItem);

        when(moduleItemService.findModuleItemByQuiz(quiz)).thenReturn(moduleItem);

        quizService.removeQuizFromModule(instructor, quiz.getId());

        assertFalse(quizRepo.findById(quiz.getId()).isPresent());
    }

    @Test
    void testRemoveQuizFromModule_CantDeleteAttemptedQuiz() {
        quizService.findQuizByIdAsInstructor(instructor, quiz.getId());

        // Add an attempt to the quiz
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(UUID.randomUUID());
        attempt.setQuiz(quiz);
        attempt.setStudent(instructor);
        quizAttemptRepo.save(attempt);

        assertThrows(CantDeleteAttemptedQuizException.class, () -> {
            quizService.removeQuizFromModule(instructor, quiz.getId());
        });
    }

    
}
