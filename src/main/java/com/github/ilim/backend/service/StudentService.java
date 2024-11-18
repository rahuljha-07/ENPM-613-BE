package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.dto.StudentCourseDto;
import com.github.ilim.backend.dto.StudentCourseModuleDto;
import com.github.ilim.backend.dto.StudentQuizDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ModuleItemType;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCantHaveQuizProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for managing student-related operations.
 * <p>
 * Provides functionalities such as retrieving course progress based on quiz attempts.
 * </p>
 *
 * @see CourseService
 * @see QuizAttemptService
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final CourseService courseService;
    private final QuizAttemptService attemptService;

    /**
     * Retrieves the quiz progress for a student in a specific course.
     * <p>
     * Calculates the number of quizzes passed and the total number of quizzes in the course.
     * </p>
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course
     * @return a {@link CourseProgressDto} containing the progress details
     * @throws CourseNotFoundException          if the course is not found or not purchased by the student
     * @throws UserCantHaveQuizProgress        if the user is an admin or the course instructor
     */
    public CourseProgressDto getCourseQuizProgress(@NonNull User student, @NonNull UUID courseId) {
        var course = (StudentCourseDto) findPurchasedCourse(student, courseId);

        if (student.getRole() == UserRole.ADMIN || course.getInstructor().getId().equals(student.getId())) {
            throw new UserCantHaveQuizProgress(student.getId(), courseId);
        }
        var quizzes = course.getModules().stream()
            .map(StudentCourseModuleDto::items)
            .flatMap(List::stream)
            .filter(item -> item.getItemType() == ModuleItemType.QUIZ && item.getPayload() != null)
            .map(item -> (StudentQuizDto) item.getPayload())
            .toList();

        var successfulAttemptCount = quizzes.stream()
            .map(quiz -> {
                return attemptService.getAllQuizAttemptsForQuiz(student, quiz.id()).stream()
                    .anyMatch(QuizAttempt::isPassed);
            })
            .filter(isPassed -> isPassed)
            .count();

        return new CourseProgressDto(courseId, (int) successfulAttemptCount, quizzes.size());
    }

    /**
     * Finds a purchased course by a student based on the course ID.
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course
     * @return the {@link Course} entity corresponding to the purchased course
     * @throws CourseNotFoundException if the course is not found or not purchased by the student
     */
    private Course findPurchasedCourse(User student, UUID courseId) {
        return courseService.findPurchasedCourses(student)
            .stream()
            .filter(c -> c.getId().equals(courseId))
            .findFirst()
            .orElseThrow(() -> new CourseNotFoundException(courseId));
    }
}
