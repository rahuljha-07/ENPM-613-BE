package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.CourseModule;

import java.util.List;
import java.util.UUID;

public record StudentCourseModuleDto(
    UUID id,
    String title,
    String description,
    int orderIndex,
    UUID courseId,
    List<CourseModuleItemDto> items
) {
    public static StudentCourseModuleDto from(CourseModule module) {

        return new StudentCourseModuleDto(
            module.getId(),
            module.getTitle(),
            module.getDescription(),
            module.getOrderIndex(),
            module.getCourseId(),
            module.getModuleItems().stream().map(CourseModuleItemDto::from).toList()
        );
    }
}
