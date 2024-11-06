package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.CourseModule;

import java.util.List;
import java.util.UUID;

public record PublicCourseModuleDto(
    UUID id,
    String title,
    String description,
    int orderIndex,
    UUID courseId
) {
    public static PublicCourseModuleDto from(CourseModule module) {

        return new PublicCourseModuleDto(
            module.getId(),
            module.getTitle(),
            module.getDescription(),
            module.getOrderIndex(),
            module.getCourseId()
        );
    }
}
