package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.CourseRejectionDto;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for administrative operations.
 * <p>
 * Provides endpoints for approving/rejecting instructor applications, blocking users,
 * managing courses, and retrieving user and course information.
 * </p>
 *
 * @see AdminService
 * @see UserService
 * @see CourseService
 */
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final CourseService courseService;

    /**
     * Approves an instructor application.
     * <p>
     * Accepts a {@link RespondToInstructorAppDto} containing the instructor application ID,
     * invokes the {@link AdminService#approveInstructorApp(UUID)} method, and returns a response
     * indicating successful approval.
     * </p>
     *
     * @param dto the request payload containing the instructor application ID
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/approve-instructor-application")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> approveInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.approveInstructorApp(dto.getInstructorApplicationId());
        return Reply.ok("Application approved");
    }

    /**
     * Rejects an instructor application.
     * <p>
     * Accepts a {@link RespondToInstructorAppDto} containing the instructor application ID and a rejection message,
     * invokes the {@link AdminService#rejectInstructorApp(UUID, String)} method, and returns a response
     * indicating successful rejection.
     * </p>
     *
     * @param dto the request payload containing the instructor application ID and rejection message
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/reject-instructor-application")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> rejectInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.rejectInstructorApp(dto.getInstructorApplicationId(), dto.getMessage());
        return Reply.ok("Application rejected");
    }

    /**
     * Blocks a user by their user ID.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link AdminService#blockUser(User, String)} method,
     * and returns a response indicating successful blocking.
     * </p>
     *
     * @param jwt    the JWT token containing the admin's authentication details
     * @param userId the ID of the user to be blocked
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/block-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> blockUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String userId) {
        var admin = userService.findById(jwt.getClaimAsString("sub"));
        adminService.blockUser(admin, userId);
        return Reply.ok("User has been blocked successfully");
    }

    /**
     * Retrieves all users in the system.
     * <p>
     * Invokes the {@link UserService#getAll()} method and returns a list of all users.
     * </p>
     *
     * @return an {@link ApiRes} containing a list of all {@link User} entities
     */
    @GetMapping("/admin/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<User>>> getAllUsers() {
        return Reply.ok(userService.getAll());
    }

    /**
     * Retrieves a specific user by their user ID.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link UserService#findByIdAsAdmin(User, String)} method,
     * and returns the requested user details.
     * </p>
     *
     * @param jwt    the JWT token containing the admin's authentication details
     * @param userId the ID of the user to retrieve
     * @return an {@link ApiRes} containing the requested {@link User} entity
     */
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<User>> getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable String userId) {
        var admin = userService.findById(jwt.getClaimAsString("sub"));
        return Reply.ok(userService.findByIdAsAdmin(admin, userId));
    }

    /**
     * Deletes a course as an admin.
     * <p>
     * Invokes the {@link CourseService#deleteCourseAsAdmin(UUID)} method to delete the specified course,
     * and returns a response indicating successful deletion.
     * </p>
     *
     * @param courseId the ID of the course to be deleted
     * @return an {@link ApiRes} containing a success message
     */
    @DeleteMapping("/admin/delete-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> deleteCourseAsAdmin(@PathVariable UUID courseId) {
        courseService.deleteCourseAsAdmin(courseId);
        return Reply.ok("Course deleted successfully");
    }

    /**
     * Approves a course submitted by an instructor.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link CourseService#approveCourse(User, UUID)} method,
     * and returns a response indicating successful approval.
     * </p>
     *
     * @param jwt      the JWT token containing the admin's authentication details
     * @param courseId the ID of the course to be approved
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/approve-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> approveCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID courseId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.approveCourse(user, courseId);
        return Reply.ok("Course Approved successfully");
    }

    /**
     * Rejects a course submitted by an instructor.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link CourseService#rejectCourse(User, UUID, CourseRejectionDto)} method,
     * and returns a response indicating successful rejection.
     * </p>
     *
     * @param jwt      the JWT token containing the admin's authentication details
     * @param courseId the ID of the course to be rejected
     * @param dto      the request payload containing rejection details
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/reject-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<String>> rejectCourse(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @Valid @RequestBody CourseRejectionDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        courseService.rejectCourse(user, courseId, dto);
        return Reply.ok("Course Rejected successfully");
    }

    /**
     * Retrieves all courses accessible to the admin.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link CourseService#findAllCourses(User)} method,
     * and returns a list of all courses.
     * </p>
     *
     * @param jwt the JWT token containing the admin's authentication details
     * @return an {@link ApiRes} containing a list of all {@link Course} entities
     */
    @GetMapping("/admin/course/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRes<Res<List<Course>>> findAllCourses(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var courses = courseService.findAllCourses(user);
        return Reply.ok(courses);
    }

    /**
     * Retrieves all courses that are waiting for admin approval.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link CourseService#findCoursesWaitingForApproval(User)} method,
     * and returns a list of courses pending approval.
     * </p>
     *
     * @param jwt the JWT token containing the admin's authentication details
     * @return an {@link ApiRes} containing a list of {@link Course} entities waiting for approval
     */
    @GetMapping("/admin/course/wait-for-approval")
    @PreAuthorize("hasRole('ADMIN')")
    ApiRes<Res<List<Course>>> findCoursesWaitingForApproval(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var courses = courseService.findCoursesWaitingForApproval(user);
        return Reply.ok(courses);
    }

    /**
     * Demotes an instructor to a student for testing purposes.
     * <p>
     * Extracts the admin's user information from the JWT, invokes the {@link CourseService#testingDemoteInstructor(User, String)} method,
     * and returns a response indicating successful demotion.
     * </p>
     * <p>
     * <strong>Note:</strong> This endpoint is intended for testing only and should not be used in production.
     * </p>
     *
     * @param jwt    the JWT token containing the admin's authentication details
     * @param userId the ID of the instructor to be demoted
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/admin/integration-tests/demote-instructor/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiRes<Res<String>> testingDemoteInstructor(@AuthenticationPrincipal Jwt jwt, @PathVariable String userId) {
        var admin = userService.findById(jwt.getClaimAsString("sub"));
        courseService.testingDemoteInstructor(admin, userId);
        return Reply.ok("[testing only] Instructor became student again");
    }

}