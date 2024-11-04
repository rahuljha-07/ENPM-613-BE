package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import jakarta.annotation.Nullable;

import java.util.UUID;

public class NoAccessToCourseContentException extends RuntimeException {

    public NoAccessToCourseContentException(@Nullable User user, UUID courseId) {
        super("User(%s) cannot access the content of the course(%s)"
            .formatted(user != null ? user.getId() : null, courseId)
        );
    }
}
