package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseModuleItemDto;
import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.dto.StudentCourseDto;
import com.github.ilim.backend.dto.StudentCourseModuleDto;
import com.github.ilim.backend.dto.StudentQuizDto;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ModuleItemType;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCantHaveQuizProgress;
import com.github.ilim.backend.repo.QuizAttemptRepo;
import com.github.ilim.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@DataJpaTest
public class StudentServiceTest {

    private StudentService studentService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private QuizAttemptService attemptService;

    @Autowired
    private UserRepo userRepo;

    @MockBean
    private QuizAttemptRepo quizAttemptRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private User instructor;
    private StudentCourseDto courseDto;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentService = new StudentService(courseService, attemptService);

        // Create student
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setRole(UserRole.STUDENT);
        student = userRepo.save(student);

        // Create instructor
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString());
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor = userRepo.save(instructor);

        // Create course DTO
        courseId = UUID.randomUUID();
        courseDto = new StudentCourseDto();
        courseDto.setId(courseId);
        courseDto.setTitle("Test Course");
        courseDto.setInstructor(instructor);
        courseDto.setModules(List.of());

        when(courseService.findPurchasedCourses(student)).thenReturn(List.of(courseDto));
    }

    @Test
    void testGetCourseQuizProgress_Success() {
        UUID quizId = UUID.randomUUID();
        StudentQuizDto quizDto = new StudentQuizDto(quizId, "Quiz Title", "d", BigDecimal.TEN, UUID.randomUUID(), List.of());
        var item = new CourseModuleItemDto();
        item.setItemType(ModuleItemType.QUIZ);
        item.setPayload(quizDto);
        item.setItemId(UUID.randomUUID());
        StudentCourseModuleDto moduleDto = new StudentCourseModuleDto(
            UUID.randomUUID(), "Module 1", "", 0, courseId, List.of(item)
        );
        courseDto.setModules(List.of(moduleDto));

        when(attemptService.getAllQuizAttemptsForQuiz(any(User.class), any(UUID.class)))
            .thenReturn(List.of(new QuizAttempt()));

        CourseProgressDto progressDto = studentService.getCourseQuizProgress(student, courseId);

        assertNotNull(progressDto);
        assertEquals(0, progressDto.completedQuizzes());
        assertEquals(1, progressDto.totalQuizzes());
    }

    @Test
    void testGetCourseQuizProgress_CourseNotFound() {
        UUID nonExistentCourseId = UUID.randomUUID();
        when(courseService.findPurchasedCourses(student)).thenReturn(List.of());

        assertThrows(CourseNotFoundException.class, () -> {
            studentService.getCourseQuizProgress(student, nonExistentCourseId);
        });
    }

    @Test
    void testGetCourseQuizProgress_UserCantHaveProgress() {
        student.setRole(UserRole.ADMIN);
        student = userRepo.save(student);

        assertThrows(UserCantHaveQuizProgress.class, () -> {
            studentService.getCourseQuizProgress(student, courseId);
        });
    }
}
