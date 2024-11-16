package com.github.ilim.backend.dto;

import java.util.UUID;

public record CourseProgressDto(
    UUID courseId,
    int completedQuizzes,
    int totalQuizzes
) {
}
