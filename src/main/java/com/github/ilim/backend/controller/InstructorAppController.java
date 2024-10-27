package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
import com.github.ilim.backend.service.InstructorAppService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/student/instructor-application")
public class InstructorAppController {

    private final InstructorAppService appService;
    private final UserService userService;

    @GetMapping
    private ApiRes<Res<List<InstructorApp>>> getCurrentUserInstructorApp(@AuthenticationPrincipal Jwt jwt) {
        return Reply.ok(appService.findByUserId(jwt.getSubject()));
    }

    @GetMapping("/all")
    private  ApiRes<Res<List<InstructorApp>>> getAll() {
        return Reply.ok(appService.findAll());
    }

    @GetMapping("/all-pending")
    private ApiRes<Res<List<InstructorApp>>> getAllPending() {
        return Reply.ok(appService.findPendingApplications());
    }

    @PostMapping("/submit")
    private ApiRes<Res<String>> submitInstructorApp(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody InstructorAppDto dto
    ) {
        var user = userService.findById(jwt.getSubject());
        if (user.getRole() == UserRole.INSTRUCTOR) {
            throw new UserAlreadyInstructorException(user.getId());
        }
        else if (user.getRole() == UserRole.ADMIN) {
            throw new AdminCannotBeInstructorException(user.getId());
        }
        else if (appService.existPendingApplicationForUser(user.getId())) {
            throw new InstructorAppAlreadyExistsException(user.getId());
        }

        var application = InstructorApp.from(dto);
        application.setUserId(user.getId());
        appService.saveInstructorApp(application);
        return Reply.ok("Application submitted successfully");
    }
}
