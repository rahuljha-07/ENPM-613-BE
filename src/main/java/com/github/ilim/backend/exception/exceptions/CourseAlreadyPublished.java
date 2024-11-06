package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;

public class CourseAlreadyPublished extends RuntimeException {

    public CourseAlreadyPublished(Course course) {
        super("Course(%s) already published".formatted(course.getId()));
    }

}
