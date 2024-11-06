package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import jakarta.annotation.Nullable;

import java.util.UUID;

public class NotCourseInstructorException extends RuntimeException {

    private static String getMessage(@Nullable User user, String itemName, UUID itemId) {
        return "User(%s) is not the instructor of the course, and thus cannot submit, edit or delete %s(%s)!".formatted(
            user != null ? user.getId() : null, itemName, itemId
        );
    }

    public NotCourseInstructorException(@Nullable User user, Course course) {
        super(getMessage(user, Course.class.getSimpleName(), course.getId()));
    }

    public NotCourseInstructorException(@Nullable User user, CourseModule module) {
        super(getMessage(user, CourseModule.class.getSimpleName(), module.getId()));
    }

    public NotCourseInstructorException(@Nullable User user, Video video) {
        super(getMessage(user, Video.class.getSimpleName(), video.getId()));
    }

    public NotCourseInstructorException(@Nullable User user, Quiz quiz) {
        super(getMessage(user, Quiz.class.getSimpleName(), quiz.getId()));
    }
}
