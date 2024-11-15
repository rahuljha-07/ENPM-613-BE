package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.dto.PublicCourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.service.CoursePurchaseService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class CourseController {

    private final CoursePurchaseService coursePurchaseService;
    private final CourseService courseService;
    private final UserService userService;

    // TODO: Use pagination for any `findAll` endpoints
    @GetMapping("/course/published")
    public ApiRes<Res<List<PublicCourseDto>>> filterPublishedCourses(
        @RequestParam(value = "contains", required = false) String contains
    ) {
        var courses = courseService.filterPublishedCourses(contains);
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

    @GetMapping("/student/course/purchased")
    @PreAuthorize("hasAnyRole('Student', 'INSTRUCTOR')")
    public ApiRes<Res<List<Course>>> findPurchasedCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaim("sub").toString());
        var courses = courseService.findPurchasedCourses(user);
        return Reply.ok(courses);
    }

    @GetMapping("/instructor/course/created")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<List<Course>>> findCreatedCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaim("sub").toString());
        var courses = courseService.findCreatedCourses(user);
        return Reply.ok(courses);
    }

    @PostMapping("/instructor/create-course")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    ApiRes<Res<String>> createCourse(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CourseDto dto) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.create(user, dto);
        return Reply.created("Course created successfully");
    }

    @PutMapping("/instructor/update-course/{courseId}")
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

    @PostMapping("/instructor/course/{courseId}/submit-for-approval")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    ApiRes<Res<String>> submitCourseForApproval(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.submitCourseForApproval(user, courseId);
        return Reply.ok("Course submitted successfully to the admin to review it.");
    }

    @PostMapping("/student/purchase-course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> purchaseCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        String checkoutUrl = coursePurchaseService.purchaseCourse(user, courseId);
        return Reply.ok(checkoutUrl);
    }

    @PutMapping("/instructor/course/{courseId}/reorder-modules")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> reorderCourseModules(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @RequestBody List<UUID> modulesOrder
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.reorderCourseModules(user, courseId, modulesOrder);
        return Reply.ok("Course modules reordered successfully.");
    }

    @PostMapping("/student/course/{courseId}/check-purchase")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<PurchaseStatus>> checkCoursePurchase(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        PurchaseStatus purchaseStatus = coursePurchaseService.checkCoursePurchase(user, courseId);
        return Reply.ok(purchaseStatus);
    }

    @PostMapping("/student/course/{courseId}/cancel-purchase")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> cancelCoursePurchase(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        coursePurchaseService.cancelCoursePurchase(user, courseId);
        return Reply.ok("All pending requests has been canceled");
    }

    @PostMapping("/admin/confirm-course-purchase")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiRes<Res<String>> simulateCoursePurchaseConfirmation(@Valid @RequestBody PaymentEventDto dto) {
        coursePurchaseService.simulateCoursePurchaseConfirmation(dto);
        return Reply.ok();
    }
}
