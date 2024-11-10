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

@RestController
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final UserService userService;

    // Submit a quiz attempt
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

    // Get quiz attempts for an instructor (all attempts by students)
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
