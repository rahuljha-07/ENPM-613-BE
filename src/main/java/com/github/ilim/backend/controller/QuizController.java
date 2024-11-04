package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.QuizDto;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.service.QuizService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    @GetMapping("/quiz/{quizId}")
    public ApiRes<Res<Quiz>> findQuizById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID quizId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var quiz = quizService.findQuizById(user, quizId);
        return Reply.ok(quiz);
    }

    @PostMapping("/instructor/module/{moduleId}/add-quiz")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> addQuizToModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID moduleId,
        @Valid @RequestBody QuizDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        quizService.addQuizToModule(user, moduleId, dto);
        return Reply.created("Quiz added successfully.");
    }

    @PutMapping("/instructor/update-quiz/{quizId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> updateQuiz(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID quizId,
        @Valid @RequestBody QuizDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        quizService.updateQuiz(user, quizId, dto);
        return Reply.ok("Quiz updated successfully");
    }

    @DeleteMapping("/instructor/delete-quiz/{quizId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> removeQuizFromModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID quizId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        quizService.removeQuizFromModule(user, quizId);
        return Reply.ok("Quiz removed successfully from the module");
    }
}
