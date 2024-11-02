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

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;


    @GetMapping("/admin/course")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<Course>>> findAllCourses() {
        var courses = courseService.findAllCourses();
        return Reply.ok(courses);
    }

    @GetMapping("/course")
    public ApiRes<Res<List<Course>>> findPublishedCourses() {
        var courses = courseService.findPublishedCourses();
        return Reply.ok(courses);
    }

    @GetMapping("/course/{courseId}")
    public ApiRes<Res<Course>> findCourseById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = jwt == null
            ? null
            : userService.findById(jwt.getClaim("sub").toString());
        var course = courseService.findCourseByIdAndUser(user, courseId);
        return Reply.ok(course);
    }

    @GetMapping("/student/course")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiRes<Res<List<Course>>> findPurchasedCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaim("sub").toString());
        var courses = courseService.findPurchasedCourses(user);
        return Reply.ok(courses);
    }

    @GetMapping("/instructor/course")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<List<Course>>> findCreatedCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaim("sub").toString());
        var courses = courseService.findCreatedCourses(user);
        return Reply.ok(courses);
    }

    @PostMapping("/instructor/course")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    ApiRes<Res<String>> createCourse(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CourseDto dto) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.create(user, dto);
        return Reply.created("Course created successfully");
    }

    @PutMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<Res<String>> updateCourse(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @Valid @RequestBody CourseDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.updateCourse(user, courseId, dto);
        return Reply.ok("Course updated successfully");
    }

    @DeleteMapping("/admin/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> deleteCourseAsAdmin(@PathVariable UUID courseId) {
        courseService.deleteCourseAsAdmin(courseId);
        return Reply.ok("Course deleted successfully");
    }

}
