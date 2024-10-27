package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.InstructorApplicationDto;
import com.github.ilim.backend.entity.InstructorApplication;
import com.github.ilim.backend.enums.Role;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.service.InstructorApplicationService;
import com.github.ilim.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class InstructorApplicationController {

    private final InstructorApplicationService appService;
    private final UserService userService;

    @GetMapping
    private List<InstructorApplication> getCurrentUserInstructorApplication(@AuthenticationPrincipal Jwt jwt) {
        return appService.findByUserId(jwt.getSubject());
    }

    @GetMapping("/all")
    private List<InstructorApplication> getAll() {
        return appService.findAll();
    }

    @PostMapping("/submit")
    private ResponseEntity<String> submitInstructorApplication(
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody InstructorApplicationDto dto
    ) {
        var user = userService.findById(jwt.getSubject());
        if (user.getRole() == Role.INSTRUCTOR) {
            throw new UserAlreadyInstructorException(user.getId());
        }
        else if (user.getRole() == Role.ADMIN) {
            throw new AdminCannotBeInstructorException(user.getId());
        }
        else if (appService.existPendingApplicationForUser(user.getId())) {
            throw new InstructorAppAlreadyExistsException(user.getId());
        }

        var application = InstructorApplication.from(dto);
        application.setUserId(user.getId());
        appService.saveInstructorApplication(application);
        return ResponseEntity.ok("Application submitted successfully");
    }
}
