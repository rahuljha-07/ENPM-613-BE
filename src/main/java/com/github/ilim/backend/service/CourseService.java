package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.dto.PublicCourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AccessDeletedCourseException;
import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.NoAccessToCourseContentException;
import com.github.ilim.backend.exception.exceptions.OnlyAdminAccessAllCourses;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.util.CourseUtil;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;
    private final CoursePurchaseService purchaseService;

    @Transactional
    public Course create(User instructor, CourseDto dto) {
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new UserCannotCreateCourseException(instructor.getRole());
        }
        var course = Course.from(dto);
        course.setInstructor(instructor);
        return courseRepo.save(course);
    }

    @Transactional
    public void approveCourse(User admin, UUID courseId) {
        var course = findCourseByIdAndUser(admin, courseId);
        assertCourseNotDeleted(course);
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepo.save(course);
    }

    @Transactional
    public void updateCourse(User user, UUID courseId, @Valid CourseDto dto) {
        var course = findCourseByIdAndUser(user, courseId);
        assertCourseNotDeleted(course);
        course.updateFrom(dto);
        courseRepo.save(course);
    }

    @Transactional
    public void saveCourse(Course course) {
        courseRepo.save(course);
    }

    public Course findPublishedCourse(UUID courseId) {
        var course = courseRepo.findByIdAndStatusAndIsDeleted(courseId, CourseStatus.PUBLISHED, false)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        return enforceCourseAccess(null, course);
    }

    public Course findCourseByIdAndUser(@Nullable User user, UUID courseId) {
        var course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        return enforceCourseAccess(user, course);
    }

    public void assertUserHasAccessToCourse(User user, Course course) {
        if (!userHasAccessToCourse(user, course)) {
            throw new UserHasNoAccessToCourseException(user, course.getId());
        }
    }

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

    public void assertUserHasAccessToCourseContent(User user, Course course) {
        if (!userHasAccessToCourseContent(user, course)) {
            throw new NoAccessToCourseContentException(user, course.getId());
        }
    }

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
        // The instructor who created the course can access ite material
        if (user.getId().equals(course.getInstructor().getId())){
            return true;
        }
        // All other users cannot access DRAFT course content
        if (course.getStatus().equals(CourseStatus.DRAFT)) {
            return false;
        }
        // Finally, a student who purchase the course can access its content
        return purchaseService.findByStudentAndCourse(user, course).isPresent();
    }

    public List<Course> findAllCourses(@NonNull User admin) {
        if (admin.getRole() != UserRole.ADMIN) {
            throw new OnlyAdminAccessAllCourses(admin.getId());
        }
        return courseRepo.findAll();
    }

    public List<PublicCourseDto> findPublishedCourses() {
        boolean isDeleted = false;
        var courses = courseRepo.findAllByStatusAndIsDeleted(CourseStatus.PUBLISHED, isDeleted);
        return CourseUtil.toPublicCourseDtos(courses);
    }

    public List<Course> findPurchasedCourses(User student) {
        boolean isDeleted = false;
        var purchasedCoursesIds = purchaseService.findAllByStudent(student).stream()
            .map(CoursePurchase::getCourse)
            .filter(Objects::nonNull)
            .map(Course::getId)
            .toList();
        var courses = courseRepo.findAllByIdInAndIsDeleted(purchasedCoursesIds, isDeleted);
        return enforceCoursesAccess(student, courses);
    }

    public List<Course> findCreatedCourses(User instructor) {
        boolean isDeleted = false;
        var courses = courseRepo.findAllByInstructorAndIsDeleted(instructor, isDeleted);
        return enforceCoursesAccess(instructor, courses);
    }

    @Transactional
    public void deleteCourseAsAdmin(UUID courseId) {
        var course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        course.setDeleted(true);
        courseRepo.save(course);
    }

    private static void assertCourseNotDeleted(@NonNull Course course) {
        if (course.isDeleted()) {
            throw new AccessDeletedCourseException(course.getId());
        }
    }

    @Transactional
    public void purchaseCourse(User student, UUID courseId) {
        var course = findPublishedCourse(courseId);
        if (purchaseService.findByStudentAndCourse(student, course).isPresent()) {
            throw new AlreadyPurchasedCourseException(student.getId(), courseId);
        }
        if (course.getInstructor().getId().equals(student.getId())) {
            throw new CantPurchaseOwnCourseException(student.getId(), course.getId());
        }
        assertCourseNotDeleted(course);
        // TODO: This should be implemented properly when the PaymentService is ready
        var purchase = new CoursePurchase();
        purchase.setCourse(course);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setPurchasePrice(course.getPrice());
        purchase.setStudent(student);
        purchase.setPaymentId(UUID.randomUUID().toString()); // TODO: should be taken from PaymentService
        purchaseService.save(purchase);
    }

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

    public List<PublicCourseDto> filterPublishedCourses(@Nullable String containsFilter) {
        var courses = findPublishedCourses();
        if (containsFilter == null) {
            return courses;
        }
        return courses.stream()
            .filter(course -> course.getTitle().toLowerCase().contains(containsFilter.toLowerCase()))
            .toList();
    }

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

    public List<Course> enforceCoursesAccess(@Nullable User user, List<Course> courses) {
        return courses.stream()
            .map(course -> enforceCourseAccess(user, course))
            .toList();
    }
}
