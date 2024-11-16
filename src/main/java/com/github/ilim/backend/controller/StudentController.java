package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseProgressDto;
import com.github.ilim.backend.service.StudentService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final UserService userService;

    @GetMapping("/student/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Res<CourseProgressDto>> getStudentQuiz(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var progress = studentService.getCourseQuizProgress(user, courseId);
        return Reply.ok(progress);
    }

}
