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

@Service
@RequiredArgsConstructor
public class StudentService {

    private final CourseService courseService;
    private final QuizAttemptService attemptService;

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

    private Course findPurchasedCourse(User student, UUID courseId) {
        return courseService.findPurchasedCourses(student)
            .stream()
            .filter(c -> c.getId().equals(courseId))
            .findFirst()
            .orElseThrow(() -> new CourseNotFoundException(courseId));
    }
}
