package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AccessDeletedCourseException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CourseRepo;
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
    public Course create(User user, CourseDto dto) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new UserCannotCreateCourseException(user.getRole());
        }
        var course = Course.from(dto);
        course.setInstructor(user);
        return courseRepo.save(course);
    }

    @Transactional
    public void updateCourse(User user, UUID courseId, @Valid CourseDto dto) {
        var course = findCourseByIdAndUser(user, courseId);
        assertCourseNotDeleted(course);
        course.updateFrom(dto);
        courseRepo.save(course);
    }

    public Course save(Course course) {
        return courseRepo.save(course);
    }

    public Course findCourseByIdAndUser(@Nullable User user, UUID courseId) {
        var course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        if (!userHasAccessToCourse(user, course)) {
            throw new UserHasNoAccessToCourseException(user, courseId);
        }
        return course;
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

    public List<Course> findAllCourses() {
        return courseRepo.findAll();
    }

    public List<Course> findPublishedCourses() {
        boolean isDeleted = false;
        return courseRepo.findAllByStatusAndIsDeleted(CourseStatus.PUBLISHED, isDeleted);
    }

    public List<Course> findPurchasedCourses(User user) {
        boolean isDeleted = false;
        var purchasedCoursesIds = purchaseService.findAllByStudent(user).stream()
            .map(CoursePurchase::getCourse)
            .filter(Objects::nonNull)
            .map(Course::getId)
            .toList();
        return courseRepo.findAllByIdInAndIsDeleted(purchasedCoursesIds, isDeleted);
    }

    public List<Course> findCreatedCourses(User instructor) {
        boolean isDeleted = false;
        return courseRepo.findAllByInstructorAndIsDeleted(instructor, isDeleted);
    }

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

    public void purchaseCourse(User student, UUID courseId) {
        var course = findCourseByIdAndUser(student, courseId);
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
}
