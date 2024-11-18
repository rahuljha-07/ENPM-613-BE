package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.service.InstructorAppService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing instructor applications.
 * <p>
 * Provides endpoints for students to submit, cancel, and retrieve their instructor applications,
 * and for admins to retrieve all instructor applications.
 * </p>
 *
 * @see InstructorAppService
 * @see UserService
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class InstructorAppController {

    private final InstructorAppService appService;
    private final UserService userService;

    /**
     * Retrieves the current user's instructor applications, optionally filtering by status.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link InstructorAppService#findByUserId(String, String)}
     * method, and returns a list of {@link InstructorApp} entities.
     * </p>
     *
     * @param jwt    the JWT token containing the user's authentication details
     * @param status an optional status filter to retrieve specific instructor applications
     * @return an {@link ApiRes} containing a list of {@link InstructorApp} entities
     */
    @GetMapping("/student/instructor-application")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<List<InstructorApp>>> getCurrentUserInstructorApp(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(value = "status", required = false) String status
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var applications = appService.findByUserId(user.getId(), status);
        return Reply.ok(applications);
    }

    /**
     * Submits a new instructor application for the authenticated user.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link InstructorAppService#saveInstructorApp(User, InstructorAppDto)}
     * method to save the application, and returns a response indicating successful submission.
     * </p>
     *
     * @param jwt the JWT token containing the user's authentication details
     * @param dto the instructor application data transfer object containing application details
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/student/instructor-application/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> submitInstructorApp(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody InstructorAppDto dto
    ) {
        var user = userService.findById(jwt.getSubject());
        appService.saveInstructorApp(user, dto);
        return Reply.created("Application submitted successfully");
    }

    /**
     * Cancels the pending instructor application for the authenticated user.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link InstructorAppService#cancelPendingInstructorApplication(User)}
     * method to cancel the application, and returns a response indicating successful cancellation.
     * </p>
     *
     * @param jwt the JWT token containing the user's authentication details
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/student/instructor-application/cancel")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> submitInstructorApp(
        @AuthenticationPrincipal Jwt jwt
    ) {
        var user = userService.findById(jwt.getSubject());
        appService.cancelPendingInstructorApplication(user);
        return Reply.created("Pending Instructor Application has been canceled successfully");
    }

    /**
     * Retrieves all instructor applications in the system.
     * <p>
     * Invokes the {@link InstructorAppService#findAll()} method and returns a list of all {@link InstructorApp} entities.
     * </p>
     *
     * @return an {@link ApiRes} containing a list of all {@link InstructorApp} entities
     */
    @GetMapping("/admin/instructor-application/all")
    @PreAuthorize("hasRole('ADMIN')")
    public  ApiRes<Res<List<InstructorApp>>> getAll() {
        return Reply.ok(appService.findAll());
    }

    /**
     * Retrieves all pending instructor applications in the system.
     * <p>
     * Invokes the {@link InstructorAppService#findPendingApplications()} method and returns a list of pending {@link InstructorApp} entities.
     * </p>
     *
     * @return an {@link ApiRes} containing a list of pending {@link InstructorApp} entities
     */
    @GetMapping("/admin/instructor-application/all-pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<InstructorApp>>> getAllPending() {
        return Reply.ok(appService.findPendingApplications());
    }

}
