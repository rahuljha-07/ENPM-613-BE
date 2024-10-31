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
        if (user.getRole() != UserRole.STUDENT) {
            throw new UserCannotCreateCourseException(user.getRole());
        }
        var course = Course.from(dto);
        course.setInstructor(user);
        return Reply.ok(courseRepo.save(course));
    }

    public Course findById(User user, UUID courseId) {
        if (!userHasAccessToCourse(user, courseId)) {
            throw new UserHasNoAccessToCourseException(user.getId(), courseId);
        }
        return courseRepo.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    private boolean userHasAccessToCourse(User user, UUID courseId) {
        return purchaseService.findByUserAndCourseId(user, courseId).isPresent();
    }
}
