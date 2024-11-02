package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.ModuleDto;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.repo.ModuleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepo moduleRepo;
    private final CourseService courseService;

    @Transactional
    public void addModuleToCourse(User instructor, UUID courseId, ModuleDto moduleDto) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = CourseModule.from(moduleDto);
        course.addCourseModule(module);
        courseService.saveCourse(course);
    }

    @Transactional
    public void deleteCourseModule(User instructor, UUID courseId, UUID moduleId) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = course.findModule(moduleId);
        course.deleteCourseModule(module);
        courseService.saveCourse(course);
    }

    public void reorderModuleItems(User instructor, UUID courseId, List<UUID> itemsOrder) {
        if (itemsOrder.isEmpty()) {
            return;
        }
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        throw new NotImplementedException();
    }

    public void updateCourseModule(User instructor, UUID courseId, UUID moduleId, ModuleDto dto) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = course.findModule(moduleId);
        module.updateFrom(dto);
        courseService.saveCourse(course);
    }
}
