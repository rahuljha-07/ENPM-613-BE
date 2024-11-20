package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.dto.CourseRejectionDto;
import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.dto.PublicCourseDto;
import com.github.ilim.backend.entity.AuditEntity;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AccessDeletedCourseException;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.CourseAlreadyPublished;
import com.github.ilim.backend.exception.exceptions.CourseIsNotWaitingApprovalException;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.CourseStatusNotDraftException;
import com.github.ilim.backend.exception.exceptions.NoAccessToCourseContentException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.OnlyAdminAccessAllCourses;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.util.CourseUtil;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service class responsible for managing courses.
 * <p>
 * Provides functionalities such as creating, updating, approving, rejecting, and deleting courses,
 * as well as managing course modules and filtering published courses. Interacts with {@link CourseRepo},
 * {@link CoursePurchaseRepo}, {@link UserService}, and {@link CourseUtil}.
 * </p>
 *
 * @see CourseRepo
 * @see CoursePurchaseRepo
 * @see UserService
 * @see CourseUtil
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;
    private final CoursePurchaseRepo purchaseRepo;
    private final UserService userService;
    private final EmailSenderService emailSenderService;

    /**
     * Creates a new course.
     * <p>
     * Validates that the user has the {@link UserRole#INSTRUCTOR} role and creates a new {@link Course}
     * entity based on the provided {@link CourseDto}.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor creating the course
     * @param dto        the {@link CourseDto} containing course details
     * @return the created {@link Course} entity
     * @throws UserCannotCreateCourseException if the user does not have the {@link UserRole#INSTRUCTOR} role
     */
    @Transactional
    public Course create(User instructor, CourseDto dto) {
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new UserCannotCreateCourseException(instructor.getRole());
        }
        var course = Course.from(dto);
        course.setInstructor(instructor);
        return courseRepo.save(course);
    }

    /**
     * Approves a course, changing its status to {@link CourseStatus#PUBLISHED}.
     * <p>
     * Validates that the course is not already published and updates its status accordingly.
     * </p>
     *
     * @param admin    the {@link User} entity representing the admin approving the course
     * @param courseId the unique identifier of the course to approve
     * @throws CourseAlreadyPublished        if the course is already published
     * @throws AccessDeletedCourseException   if the course has been deleted
     * @throws CourseNotFoundException        if the course does not exist
     */
    @Transactional
    public void approveCourse(User admin, UUID courseId) {
        var course = findCourseByIdAndUser(admin, courseId);
        assertCourseNotDeleted(course);
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new CourseAlreadyPublished(course);
        }
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepo.save(course);
        var instructor = course.getInstructor();
        var emailRequest = EmailDto.builder()
            .subject("You course is online!")
            .content("Ilim admin approved your course titled '%s' and it's now published!".formatted(course.getTitle()))
            .toAddress(instructor.getEmail())
            .build();
        emailSenderService.sendEmail(emailRequest);
    }

    /**
     * Rejects a course, changing its status back to {@link CourseStatus#DRAFT}.
     * <p>
     * Validates that the course is waiting for approval and updates its status accordingly.
     * </p>
     *
     * @param admin    the {@link User} entity representing the admin rejecting the course
     * @param courseId the unique identifier of the course to reject
     * @param dto      the {@link CourseRejectionDto} containing the reason for rejection
     * @throws CourseIsNotWaitingApprovalException if the course is not in the {@link CourseStatus#WAIT_APPROVAL} state
     * @throws AccessDeletedCourseException        if the course has been deleted
     * @throws CourseNotFoundException             if the course does not exist
     */
    @Transactional
    public void rejectCourse(User admin, UUID courseId, @Valid CourseRejectionDto dto) {
        var course = findCourseByIdAndUser(admin, courseId);
        assertCourseNotDeleted(course);
        if (course.getStatus() != CourseStatus.WAIT_APPROVAL) {
            throw new CourseIsNotWaitingApprovalException(course.getId(), course.getStatus());
        }
        course.setStatus(CourseStatus.DRAFT);
        courseRepo.save(course);
        var instructor = course.getInstructor();
        var emailRequest = EmailDto.builder()
            .subject("You course submission is rejected!")
            .content("Ilim admin rejected your course titled '%s' and moved it back to DRAFT status. " +
                "Checkout the reason below and update your course then submit it again.\n Rejection Reason:\n" + dto.getReason())
            .toAddress(instructor.getEmail())
            .build();
        emailSenderService.sendEmail(emailRequest);
    }

    /**
     * Updates an existing course with new details.
     * <p>
     * Validates that the user has access to the course and updates the course based on the provided {@link CourseDto}.
     * </p>
     *
     * @param user     the {@link User} entity representing the user updating the course
     * @param courseId the unique identifier of the course to update
     * @param dto      the {@link CourseDto} containing updated course details
     * @throws AccessDeletedCourseException     if the course has been deleted
     * @throws CourseNotFoundException          if the course does not exist
     * @throws UserHasNoAccessToCourseException if the user does not have access to the course
     */
    @Transactional
    public void updateCourse(User user, UUID courseId, @Valid CourseDto dto) {
        var course = findCourseByIdAndUser(user, courseId);
        assertCourseNotDeleted(course);
        course.updateFrom(dto);
        courseRepo.save(course);
    }

    /**
     * Saves or updates a course entity.
     *
     * @param course the {@link Course} entity to save
     */
    @Transactional
    public void saveCourse(Course course) {
        courseRepo.save(course);
    }

    /**
     * Finds a published course by its unique identifier.
     *
     * @param courseId the unique identifier of the course
     * @return the {@link Course} entity representing the published course
     * @throws CourseNotFoundException      if the course does not exist or is not published
     * @throws AccessDeletedCourseException if the course has been deleted
     */
    public Course findPublishedCourse(UUID courseId) {
        var course = courseRepo.findByIdAndStatusAndIsDeleted(courseId, CourseStatus.PUBLISHED, false)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        return enforceCourseAccess(null, course);
    }

    /**
     * Finds a course by its unique identifier and enforces access controls based on the user.
     *
     * @param user     the {@link User} entity representing the user (can be {@code null} for public access)
     * @param courseId the unique identifier of the course
     * @return the {@link Course} entity with enforced access controls
     * @throws CourseNotFoundException      if the course does not exist
     * @throws AccessDeletedCourseException if the course has been deleted
     * @throws UserHasNoAccessToCourseException if the user does not have access to the course
     */
    public Course findCourseByIdAndUser(@Nullable User user, UUID courseId) {
        var course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        return enforceCourseAccess(user, course);
    }

    /**
     * Asserts that a user has access to a specific course.
     *
     * @param user   the {@link User} entity representing the user
     * @param course the {@link Course} entity representing the course
     * @throws UserHasNoAccessToCourseException if the user does not have access to the course
     */
    public void assertUserHasAccessToCourse(User user, Course course) {
        if (!userHasAccessToCourse(user, course)) {
            throw new UserHasNoAccessToCourseException(user, course.getId());
        }
    }

    /**
     * Checks if a user has access to a specific course.
     *
     * @param user   the {@link User} entity representing the user
     * @param course the {@link Course} entity representing the course
     * @return {@code true} if the user has access, {@code false} otherwise
     */
    private boolean userHasAccessToCourse(@Nullable User user, Course course) {
        assertCourseNotDeleted(course);
        // No need for further checks if it's a published course
        if (course.getStatus().equals(CourseStatus.PUBLISHED)) {
            return true;
        }
        // DRAFT courses are not accessible publicly
        if (user == null) {
            return false;
        }
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }
        // is user is the creator of the course?
        return user.getId().equals(course.getInstructor().getId());
    }

    /**
     * Asserts that a user has access to the content of a specific course.
     *
     * @param user   the {@link User} entity representing the user
     * @param course the {@link Course} entity representing the course
     * @throws NoAccessToCourseContentException if the user does not have access to the course content
     */
    public void assertUserHasAccessToCourseContent(User user, Course course) {
        if (!userHasAccessToCourseContent(user, course)) {
            throw new NoAccessToCourseContentException(user, course.getId());
        }
    }

    /**
     * Checks if a user has access to the content of a specific course.
     *
     * @param user   the {@link User} entity representing the user
     * @param course the {@link Course} entity representing the course
     * @return {@code true} if the user has access, {@code false} otherwise
     */
    private boolean userHasAccessToCourseContent(@Nullable User user, @NonNull Course course) {
        assertCourseNotDeleted(course);
        // Visitors cannot access any course content
        if (user == null) {
            return false;
        }
        // Admin can access everything
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }
        // The instructor who created the course can access its material
        if (user.getId().equals(course.getInstructor().getId())){
            return true;
        }
        // All other users cannot access non-published course content
        if (!course.getStatus().equals(CourseStatus.PUBLISHED)) {
            return false;
        }
        // Finally, a student who purchased the course can access its content
        return CoursePurchaseService.didStudentPurchaseCourse(purchaseRepo, user, course);
    }

    /**
     * Retrieves all courses, accessible only to admins.
     *
     * @param admin the {@link User} entity representing the admin
     * @return a list of all {@link Course} entities
     * @throws OnlyAdminAccessAllCourses if the user is not an admin
     */
    public List<Course> findAllCourses(@NonNull User admin) {
        if (admin.getRole() != UserRole.ADMIN) {
            throw new OnlyAdminAccessAllCourses(admin.getId());
        }
        return courseRepo.findAll(AuditEntity.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Retrieves all courses that are waiting for approval.
     *
     * @param user the {@link User} entity representing the user (admin)
     * @return a list of {@link Course} entities with {@link CourseStatus#WAIT_APPROVAL}
     */
    public List<Course> findCoursesWaitingForApproval(User user) {
        return findAllCourses(user).stream()
            .filter(course -> course.getStatus() == CourseStatus.WAIT_APPROVAL)
            .toList();
    }

    /**
     * Retrieves all published courses and converts them to {@link PublicCourseDto}.
     *
     * @return a list of {@link PublicCourseDto} representing published courses
     */
    public List<PublicCourseDto> findPublishedCourses() {
        boolean isDeleted = false;
        var courses = courseRepo.findAllByStatusAndIsDeleted(
            CourseStatus.PUBLISHED, isDeleted, AuditEntity.SORT_BY_CREATED_AT_DESC
        );
        return CourseUtil.toPublicCourseDtos(courses);
    }

    /**
     * Retrieves all courses purchased by a student.
     *
     * @param student the {@link User} entity representing the student
     * @return a list of {@link Course} entities that the student has purchased
     */
    public List<Course> findPurchasedCourses(User student) {
        boolean isDeleted = false;
        var purchasedCoursesIds = purchaseRepo.findAllByStudent(student, AuditEntity.SORT_BY_CREATED_AT_DESC).stream()
            .filter(purchase -> purchase.getStatus() == PurchaseStatus.SUCCEEDED)
            .map(CoursePurchase::getCourse)
            .filter(Objects::nonNull)
            .map(Course::getId)
            .toList();
        var courses = courseRepo.findAllByIdInAndIsDeleted(purchasedCoursesIds, isDeleted);
        return enforceCoursesAccess(student, courses);
    }

    /**
     * Retrieves all courses created by an instructor.
     *
     * @param instructor the {@link User} entity representing the instructor
     * @return a list of {@link Course} entities created by the instructor
     */
    public List<Course> findCreatedCourses(User instructor) {
        boolean isDeleted = false;
        var courses = courseRepo.findAllByInstructorAndIsDeleted(
            instructor, isDeleted, AuditEntity.SORT_BY_CREATED_AT_DESC
        );
        return enforceCoursesAccess(instructor, courses);
    }

    /**
     * Deletes a course as an admin by marking it as deleted.
     *
     * @param courseId the unique identifier of the course to delete
     * @throws CourseNotFoundException      if the course does not exist
     * @throws AccessDeletedCourseException if the course is already deleted
     */
    @Transactional
    public void deleteCourseAsAdmin(UUID courseId) {
        var course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        course.setDeleted(true);
        courseRepo.save(course);
    }

    /**
     * Asserts that a course has not been deleted.
     *
     * @param course the {@link Course} entity to check
     * @throws AccessDeletedCourseException if the course has been deleted
     */
    private static void assertCourseNotDeleted(@NonNull Course course) {
        if (course.isDeleted()) {
            throw new AccessDeletedCourseException(course.getId());
        }
    }

    /**
     * Reorders the modules of a course based on the provided module order.
     *
     * @param instructor  the {@link User} entity representing the instructor
     * @param courseId    the unique identifier of the course
     * @param modulesOrder a list of {@link UUID} representing the new order of modules
     * @throws BadRequestException           if the number of modules does not match
     * @throws CourseModuleNotFoundException if a module ID does not exist within the course
     * @throws CourseNotFoundException       if the course does not exist
     * @throws AccessDeletedCourseException  if the course has been deleted
     */
    @Transactional
    public void reorderCourseModules(User instructor, UUID courseId, List<UUID> modulesOrder) {
        var course = findCourseByIdAndUser(instructor, courseId);
        if (modulesOrder.size() != course.getCourseModules().size()) {
            throw new BadRequestException(
                "reorderCourseModules request must have exactly the same number of modules as the parent course!"
            );
        }
        var modulesInNewOrder = modulesOrder.stream()
            .map(id -> {
                var module = course.findModule(id);
                if (module == null) {
                    throw new CourseModuleNotFoundException(id);
                }
                return module;
            })
            .toList();
        course.getCourseModules().clear();
        course.getCourseModules().addAll(modulesInNewOrder);
        saveCourse(course);
    }

    /**
     * Filters published courses based on a provided string filter.
     *
     * @param containsFilter the string to filter course titles by (case-insensitive)
     * @return a list of {@link PublicCourseDto} representing the filtered published courses
     */
    public List<PublicCourseDto> filterPublishedCourses(@Nullable String containsFilter) {
        var courses = findPublishedCourses();
        if (containsFilter == null) {
            return courses;
        }
        return courses.stream()
            .filter(course -> course.getTitle().toLowerCase().contains(containsFilter.toLowerCase()))
            .toList();
    }

    /**
     * Enforces access controls on a course based on the user's permissions.
     *
     * @param user   the {@link User} entity representing the user (can be {@code null} for public access)
     * @param course the {@link Course} entity representing the course
     * @return the {@link Course} entity with enforced access controls
     * @throws IllegalStateException          if access controls fail unexpectedly
     * @throws NoAccessToCourseContentException if the user does not have access to the course content
     */
    public Course enforceCourseAccess(@Nullable User user, Course course) {
        if (userHasAccessToCourseContent(user, course)) {
            if (user == null) {
                throw new IllegalStateException("userHasAccessToCourseContent failed to work!");
            }
            // hide answers for students of the course
            if (!user.getRole().equals(UserRole.ADMIN) && !user.getId().equals(course.getInstructor().getId())) {
                return CourseUtil.toStudentCourseDto(course);
            }
            return course;
        }
        assertUserHasAccessToCourse(user, course);
        return CourseUtil.toPublicCourseDto(course);
    }

    /**
     * Enforces access controls on a list of courses based on the user's permissions.
     *
     * @param user    the {@link User} entity representing the user (can be {@code null} for public access)
     * @param courses the list of {@link Course} entities to enforce access controls on
     * @return a list of {@link Course} entities with enforced access controls
     */
    public List<Course> enforceCoursesAccess(@Nullable User user, List<Course> courses) {
        return courses.stream()
            .map(course -> enforceCourseAccess(user, course))
            .toList();
    }

    /**
     * Submits a course for approval.
     * <p>
     * Changes the course status to {@link CourseStatus#WAIT_APPROVAL} if it is currently in {@link CourseStatus#DRAFT}.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor submitting the course
     * @param courseId   the unique identifier of the course to submit
     * @throws NotCourseInstructorException   if the instructor does not own the course
     * @throws CourseStatusNotDraftException if the course is not in the {@link CourseStatus#DRAFT} state
     * @throws CourseNotFoundException        if the course does not exist
     * @throws AccessDeletedCourseException   if the course has been deleted
     */
    public void submitCourseForApproval(User instructor, UUID courseId) {
        var course = findCourseByIdAndUser(instructor, courseId);
        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new NotCourseInstructorException(instructor, course);
        }
        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new CourseStatusNotDraftException(course);
        }
        course.setStatus(CourseStatus.WAIT_APPROVAL);
        courseRepo.save(course);
    }

    /**
     * Demotes an instructor to a student. Primarily used for integration testing or admin gifting.
     *
     * @param admin  the {@link User} entity representing the admin performing the demotion
     * @param userId the unique identifier of the user to demote
     */
    public void testingDemoteInstructor(@NonNull User admin, @NonNull String userId) {
        var user = userService.findById(userId);
        userService.demoteToStudent(user);
    }
}
