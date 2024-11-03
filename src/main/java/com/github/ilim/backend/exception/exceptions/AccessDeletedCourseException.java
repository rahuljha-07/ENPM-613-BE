package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.User;
import jakarta.annotation.Nullable;

import java.util.UUID;

public class AccessDeletedCourseException extends RuntimeException {
    public AccessDeletedCourseException(UUID courseId) {
        super("You can't access a deleted course(%s)".formatted(courseId));
    }
}
