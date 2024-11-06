package com.github.ilim.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.exception.exceptions.CantCreatePublicCourseException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PublicCourseDto extends Course {

    private List<PublicCourseModuleDto> modules = new ArrayList<>();

    @Override
    @JsonIgnore
    public List<CourseModule> getCourseModules() {
        return super.getCourseModules();
    }

    public static PublicCourseDto from(Course course) {
        if (course == null || course.isDeleted() || course.getStatus().equals(CourseStatus.DRAFT)) {
            throw new CantCreatePublicCourseException(course);
        }
        var dto = new PublicCourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setDescription(course.getDescription());
        dto.setTranscriptUrl(course.getTranscriptUrl());
        dto.setInstructor(course.getInstructor());
        dto.setPrice(course.getPrice());
        dto.setStatus(course.getStatus());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        setPublicCourseModules(dto, course.getCourseModules());
        return dto;
    }

    private static void setPublicCourseModules(PublicCourseDto courseDto, List<CourseModule> courseModules) {
        for (var courseModule : courseModules) {
            var moduleDto = PublicCourseModuleDto.from(courseModule);
            courseDto.getModules().add(moduleDto);
        }
    }
}
