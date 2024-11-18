package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.QuizDto;
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

/**
 * REST controller responsible for managing quizzes.
 * <p>
 * Provides endpoints for retrieving quizzes, adding new quizzes to modules,
 * updating existing quizzes, and removing quizzes from modules.
 * </p>
 *
 * @see QuizService
 * @see UserService
 */
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    /**
     * Retrieves a quiz by its unique identifier.
     * <p>
     * Fetches the quiz details for the specified quiz ID, accessible to authenticated users.
     * </p>
     *
     * @param jwt    the JWT token representing the authenticated user
     * @param quizId the unique identifier of the quiz to retrieve
     * @return an {@link ApiRes} containing the {@link QuizDto} representing the quiz details
     */
    @GetMapping("/quiz/{quizId}")
    public ApiRes<Res<QuizDto>> findQuizById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID quizId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var quizDto = quizService.getQuizDtoByQuizId(user, quizId);
        return Reply.ok(quizDto);
    }

    /**
     * Adds a new quiz to a specific module.
     * <p>
     * Accepts a {@link QuizDto} containing quiz details, associates it with the specified module ID,
     * and adds the quiz to the module.
     * </p>
     *
     * @param jwt      the JWT token representing the authenticated instructor
     * @param moduleId the unique identifier of the module to which the quiz will be added
     * @param dto      the quiz data transfer object containing quiz details
     * @return an {@link ApiRes} containing a success message upon successful addition of the quiz
     */
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

    /**
     * Updates an existing quiz with new details.
     * <p>
     * Accepts a {@link QuizDto} containing updated quiz details, associates it with the specified quiz ID,
     * and updates the quiz accordingly.
     * </p>
     *
     * @param jwt    the JWT token representing the authenticated instructor
     * @param quizId the unique identifier of the quiz to update
     * @param dto    the quiz data transfer object containing updated quiz details
     * @return an {@link ApiRes} containing a success message upon successful update of the quiz
     */
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

    /**
     * Removes a quiz from a specific module.
     * <p>
     * Deletes the association between the specified quiz ID and its module, effectively removing the quiz.
     * </p>
     *
     * @param jwt    the JWT token representing the authenticated instructor
     * @param quizId the unique identifier of the quiz to remove
     * @return an {@link ApiRes} containing a success message upon successful removal of the quiz
     */
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
