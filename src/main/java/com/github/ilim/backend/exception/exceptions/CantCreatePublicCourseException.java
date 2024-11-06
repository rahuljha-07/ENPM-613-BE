package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.CourseStatus;

public class CantCreatePublicCourseException extends RuntimeException {

    public CantCreatePublicCourseException(Course course) {
        super("Cannot create a PublicCourseDto from a %s Course(%s)"
            .formatted(course == null
                    ? null
                    : course.isDeleted()
                    ? "deleted"
                    : !course.getStatus().equals(CourseStatus.PUBLISHED)
                    ? "non-published"
                    : "unknown reason",
                course != null
                    ? course.getId()
                    : null
            )
        );
    }

}
