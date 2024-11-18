package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.QuizAttemptDto;
import com.github.ilim.backend.dto.QuizAttemptResultDto;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.service.QuizAttemptService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller responsible for managing quiz attempts.
 * <p>
 * Provides endpoints for students to submit quiz attempts, retrieve their attempts,
 * and allows instructors and admins to view all quiz attempts for a specific quiz.
 * </p>
 *
 * @see QuizAttemptService
 * @see UserService
 */
@RestController
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final UserService userService;

    /**
     * Submits a quiz attempt by a student.
     * <p>
     * Accepts a {@link QuizAttemptDto} containing the student's answers and quiz details,
     * processes the attempt, and returns the result.
     * </p>
     *
     * @param jwt the JWT token representing the authenticated user
     * @param dto the quiz attempt data transfer object containing quiz details and answers
     * @return an {@link ApiRes} containing the {@link QuizAttemptResultDto} with the results of the attempt
     */
    @PostMapping("/student/attempt-quiz")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<QuizAttemptResultDto>> submitQuizAttempt(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody QuizAttemptDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var result = quizAttemptService.processQuizAttempt(user, dto);
        return Reply.ok(result);
    }

    /**
     * Retrieves all quiz attempts for a specific quiz by a student.
     * <p>
     * Fetches all attempts made by the authenticated student for the given quiz ID.
     * </p>
     *
     * @param jwt    the JWT token representing the authenticated user
     * @param quizId the unique identifier of the quiz
     * @return an {@link ApiRes} containing a list of {@link QuizAttemptResultDto} representing all attempts
     */
    @GetMapping("/student/quiz-attempt/{quizId}")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    public ApiRes<Res<List<QuizAttemptResultDto>>> getAllQuizAttempts(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID quizId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var attemptDto = quizAttemptService.getAllStudentQuizAttempts(user, quizId);
        return Reply.ok(attemptDto);
    }

    /**
     * Retrieves the last quiz attempt for a specific quiz by a student.
     * <p>
     * Fetches the most recent attempt made by the authenticated student for the given quiz ID.
     * </p>
     *
     * @param jwt    the JWT token representing the authenticated user
     * @param quizId the unique identifier of the quiz
     * @return an {@link ApiRes} containing the {@link QuizAttemptResultDto} representing the last attempt
     */
    @GetMapping("/student/quiz-attempt/{quizId}/last")
    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    public ApiRes<Res<QuizAttemptResultDto>> getLastQuizAttempt(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID quizId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var attemptDto = quizAttemptService.getLastStudentQuizAttempt(user, quizId);
        return Reply.ok(attemptDto);
    }

    /**
     * Retrieves all quiz attempts for a specific quiz by all students.
     * <p>
     * Allows instructors and admins to view all quiz attempts made by students for the given quiz ID.
     * </p>
     *
     * @param jwt    the JWT token representing the authenticated user (instructor or admin)
     * @param quizId the unique identifier of the quiz
     * @return an {@link ApiRes} containing a list of {@link QuizAttempt} representing all student attempts
     */
    @GetMapping("/instructor/quiz-attempt/{quizId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<Res<List<QuizAttempt>>> getAllQuizAttemptsForQuiz(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID quizId
    ) {
        var instructor = userService.findById(jwt.getClaimAsString("sub"));
        var attempts = quizAttemptService.getAllQuizAttemptsForQuiz(instructor, quizId);
        return Reply.ok(attempts);
    }
}
