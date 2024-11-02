package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        course.updateFrom(dto);
        courseRepo.save(course);
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
        return courseRepo.findAllByStatus(CourseStatus.PUBLISHED);
    }

    public List<Course> findPurchasedCourses(User user) {
        var purchasedCoursesIds = purchaseService.findAllByStudent(user).stream()
            .map(CoursePurchase::getCourse)
            .filter(Objects::nonNull)
            .map(Course::getId)
            .toList();
        return courseRepo.findAllByIdIn(purchasedCoursesIds);
    }

    public List<Course> findCreatedCourses(User instructor) {
        return courseRepo.findAllByInstructor(instructor);
    }
}
