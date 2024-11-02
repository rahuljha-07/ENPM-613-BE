package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo courseRepo;
    private final CoursePurchaseService purchaseService;

    @Transactional
    public ApiRes<Res<Course>> create(User user, CourseDto dto) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new UserCannotCreateCourseException(user.getRole());
        }
        var course = Course.from(dto);
        course.setInstructor(user);
        return Reply.ok(courseRepo.save(course));
    }

    public Course findById(User user, UUID courseId) {
        var course = courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
        if (!userHasAccessToCourse(user, course)) {
            throw new UserHasNoAccessToCourseException(user.getId(), courseId);
        }
        return course;
    }

    private boolean userHasAccessToCourse(User user, Course course) {
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }
        // is user is the creator of the course?
        if (user.getId().equals(course.getInstructor().getId())) {
            return true;
        }
        return purchaseService.findByUserAndCourseId(user, course).isPresent();
    }
}
