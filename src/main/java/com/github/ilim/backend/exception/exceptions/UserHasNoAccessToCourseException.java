package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.User;
import jakarta.annotation.Nullable;

import java.util.UUID;

public class UserHasNoAccessToCourseException extends RuntimeException {
    public UserHasNoAccessToCourseException(@Nullable User user, UUID courseId) {
        super("User with id '%s' doesn't have access to course with id '%s'"
            .formatted(user == null ? "null" : user.getId(), courseId));
    }
}
