package com.github.ilim.backend.dto;

import java.util.UUID;

public record CourseQuizProgress(
    UUID courseId,
    int completedQuizzes,
    int totalQuizzes
) {
}
