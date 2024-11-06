package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.dto.RespondToInstructorAppDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.AdminService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final CourseService courseService;

    @PostMapping("/admin/approve-instructor-application")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> approveInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.approveInstructorApp(dto.getInstructorApplicationId());
        return Reply.ok("Application approved");
    }

    @PostMapping("/admin/reject-instructor-application")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> rejectInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.rejectInstructorApp(dto.getInstructorApplicationId(), dto.getMessage());
        return Reply.ok("Application rejected");
    }

    @PostMapping("/admin/block-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> blockUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String userId) {
        var admin = userService.findById(jwt.getClaimAsString("sub"));
        adminService.blockUser(admin, userId);
        return Reply.ok("User has been blocked successfully");
    }

    @GetMapping("/admin/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<User>>> getAllUsers() {
        return Reply.ok(userService.getAll());
    }

    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<User>> getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String userId) {
        var admin = userService.findById(jwt.getClaimAsString("sub"));
        return Reply.ok(userService.findByIdAsAdmin(admin, userId));
    }

    @DeleteMapping("/admin/delete-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> deleteCourseAsAdmin(@PathVariable UUID courseId) {
        courseService.deleteCourseAsAdmin(courseId);
        return Reply.ok("Course deleted successfully");
    }

    @PostMapping("/admin/approve-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> approveCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.approveCourse(user, courseId);
        return Reply.ok("Course Approved successfully");
    }

    @GetMapping("/admin/course/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<Course>>> findAllCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var courses = courseService.findAllCourses(user);
        return Reply.ok(courses);
    }

}
