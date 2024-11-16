package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.service.InstructorAppService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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

@RequiredArgsConstructor
@RestController
@RequestMapping
public class InstructorAppController {

    private final InstructorAppService appService;
    private final UserService userService;

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

    @PostMapping("/student/instructor-application/cancel")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> submitInstructorApp(
        @AuthenticationPrincipal Jwt jwt
    ) {
        var user = userService.findById(jwt.getSubject());
        appService.cancelPendingInstructorApplication(user);
        return Reply.created("Pending Instructor Application has been canceled successfully");
    }

    @GetMapping("/admin/instructor-application/all")
    @PreAuthorize("hasRole('ADMIN')")
    public  ApiRes<Res<List<InstructorApp>>> getAll() {
        return Reply.ok(appService.findAll());
    }

    @GetMapping("/admin/instructor-application/all-pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<InstructorApp>>> getAllPending() {
        return Reply.ok(appService.findPendingApplications());
    }

}
