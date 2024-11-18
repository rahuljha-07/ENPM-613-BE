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

/**
 * REST controller for managing courses.
 * <p>
 * Provides endpoints for filtering published courses, retrieving course details, managing course purchases,
 * creating and updating courses, and handling course module operations.
 * </p>
 *
 * @see CourseService
 * @see CoursePurchaseService
 * @see UserService
 */
@RequiredArgsConstructor
@RestController
public class CourseController {

    private final CoursePurchaseService coursePurchaseService;
    private final CourseService courseService;
    private final UserService userService;

    /**
     * Retrieves a list of published courses, optionally filtering by a search keyword.
     * <p>
     * Invokes the {@link CourseService#filterPublishedCourses(String)} method to obtain the list of
     * {@link PublicCourseDto} based on the provided search criteria.
     * </p>
     * <p>
     * <strong>Note:</strong> Pagination should be implemented for this endpoint to handle large datasets.
     * </p>
     *
     * @param contains an optional search keyword to filter courses by title or description
     * @return an {@link ApiRes} containing a list of {@link PublicCourseDto} representing published courses
     */
    @GetMapping("/course/published")
    public ApiRes<Res<List<PublicCourseDto>>> filterPublishedCourses(
        @RequestParam(value = "contains", required = false) String contains
    ) {
        var courses = courseService.filterPublishedCourses(contains);
        return Reply.ok(courses);
    }

    /**
     * Retrieves detailed information about a specific course.
     * <p>
     * Extracts the user's information from the JWT (if available), invokes the {@link CourseService#findCourseByIdAndUser(User, UUID)} method,
     * and returns the detailed {@link Course} entity.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details (optional)
     * @param courseId the ID of the course to retrieve
     * @return an {@link ApiRes} containing the {@link Course} entity
     */
    @GetMapping("/course/{courseId}")
    public ApiRes<Res<Course>> findCourseById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = jwt == null
            ? null
            : userService.findById(jwt.getClaim("sub").toString());
        var course = courseService.findCourseByIdAndUser(user, courseId);
        return Reply.ok(course);
    }

    /**
     * Retrieves a list of courses purchased by the authenticated user.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CourseService#findPurchasedCourses(User)} method,
     * and returns a list of purchased {@link Course} entities.
     * </p>
     *
     * @param jwt the JWT token containing the user's authentication details
     * @return an {@link ApiRes} containing a list of {@link Course} entities purchased by the user
     */
    @GetMapping("/student/course/purchased")
    @PreAuthorize("hasAnyRole('Student', 'INSTRUCTOR')")
    public ApiRes<Res<List<Course>>> findPurchasedCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaimAsString("sub").toString());
        var courses = courseService.findPurchasedCourses(user);
        return Reply.ok(courses);
    }

    /**
     * Retrieves a list of courses created by the authenticated instructor.
     * <p>
     * Extracts the instructor's information from the JWT, invokes the {@link CourseService#findCreatedCourses(User)} method,
     * and returns a list of created {@link Course} entities.
     * </p>
     *
     * @param jwt the JWT token containing the instructor's authentication details
     * @return an {@link ApiRes} containing a list of {@link Course} entities created by the instructor
     */
    @GetMapping("/instructor/course/created")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<List<Course>>> findCreatedCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaimAsString("sub").toString());
        var courses = courseService.findCreatedCourses(user);
        return Reply.ok(courses);
    }

    /**
     * Creates a new course as an instructor.
     * <p>
     * Extracts the instructor's information from the JWT, invokes the {@link CourseService#create(User, CourseDto)} method,
     * and returns a response indicating successful course creation.
     * </p>
     *
     * @param jwt the JWT token containing the instructor's authentication details
     * @param dto the course creation request payload containing course details
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/instructor/create-course")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    ApiRes<Res<String>> createCourse(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CourseDto dto) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.create(user, dto);
        return Reply.created("Course created successfully");
    }

    /**
     * Updates an existing course as an instructor or admin.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CourseService#updateCourse(User, UUID, CourseDto)} method,
     * and returns a response indicating successful course update.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param courseId the ID of the course to be updated
     * @param dto      the course update request payload containing updated course details
     * @return an {@link ApiRes} containing a success message
     */
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

    /**
     * Submits a course for admin approval.
     * <p>
     * Extracts the instructor's information from the JWT, invokes the {@link CourseService#submitCourseForApproval(User, UUID)} method,
     * and returns a response indicating successful submission.
     * </p>
     *
     * @param jwt      the JWT token containing the instructor's authentication details
     * @param courseId the ID of the course to be submitted for approval
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/instructor/course/{courseId}/submit-for-approval")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    ApiRes<Res<String>> submitCourseForApproval(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.submitCourseForApproval(user, courseId);
        return Reply.ok("Course submitted successfully to the admin to review it.");
    }

    /**
     * Initiates the purchase process for a course.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CoursePurchaseService#purchaseCourse(User, UUID)} method,
     * and returns the checkout URL for payment.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param courseId the ID of the course to be purchased
     * @return an {@link ApiRes} containing the checkout URL as a string
     */
    @PostMapping("/student/purchase-course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> purchaseCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        String checkoutUrl = coursePurchaseService.purchaseCourse(user, courseId);
        return Reply.ok(checkoutUrl);
    }

    /**
     * Reorders the modules within a course.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CourseService#reorderCourseModules(User, UUID, List)} method,
     * and returns a response indicating successful reordering.
     * </p>
     *
     * @param jwt         the JWT token containing the user's authentication details
     * @param courseId    the ID of the course whose modules are to be reordered
     * @param modulesOrder the list of module IDs in the desired order
     * @return an {@link ApiRes} containing a success message
     */
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

    /**
     * Checks the purchase status of a course for the authenticated user.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CoursePurchaseService#checkCoursePurchase(User, UUID)} method,
     * and returns the purchase status.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param courseId the ID of the course to check purchase status for
     * @return an {@link ApiRes} containing the {@link PurchaseStatus} of the course
     */
    @PostMapping("/student/course/{courseId}/check-purchase")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<PurchaseStatus>> checkCoursePurchase(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        PurchaseStatus purchaseStatus = coursePurchaseService.checkCoursePurchase(user, courseId);
        return Reply.ok(purchaseStatus);
    }

    /**
     * Cancels the purchase of a course for the authenticated user.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link CoursePurchaseService#cancelCoursePurchase(User, UUID)} method,
     * and returns a response indicating successful cancellation.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param courseId the ID of the course whose purchase is to be canceled
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/student/course/{courseId}/cancel-purchase")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> cancelCoursePurchase(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        coursePurchaseService.cancelCoursePurchase(user, courseId);
        return Reply.ok("All pending requests has been canceled");
    }

    /**
     * Simulates the confirmation of a course purchase.
     * <p>
     * Accepts a {@link PaymentEventDto} containing payment event details, invokes the
     * {@link CoursePurchaseService#simulateCoursePurchaseConfirmation(PaymentEventDto)} method,
     * and returns a response indicating successful simulation.
     * </p>
     *
     * @param dto the payment event data transfer object containing confirmation details
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/confirm-course-purchase")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiRes<Res<String>> simulateCoursePurchaseConfirmation(@Valid @RequestBody PaymentEventDto dto) {
        coursePurchaseService.simulateCoursePurchaseConfirmation(dto);
        return Reply.ok();
    }
}