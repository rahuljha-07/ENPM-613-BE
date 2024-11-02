package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.service.CourseService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/{courseId}")
    public ApiRes<Res<Course>> findById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaim("sub").toString());
        var course = courseService.findById(user, courseId);
        return Reply.ok(course);
    }

    @PostMapping
    ApiRes<Res<String>> createCourse(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CourseDto dto) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.create(user, dto);
        return Reply.created("Course created successfully");
    }
}
