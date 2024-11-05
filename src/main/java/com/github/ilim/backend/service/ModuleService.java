package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.ModuleDto;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.repo.ModuleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.lang.NonNull;
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
    public void deleteCourseModule(User instructor, UUID moduleId) {
        var module = findModuleByIdAsInstructor(instructor, moduleId);
        var course = module.getCourse();
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

    public void updateCourseModule(User instructor, UUID moduleId, ModuleDto dto) {
        var module = findModuleByIdAsInstructor(instructor, moduleId);
        module.updateFrom(dto);
        courseService.saveCourse(module.getCourse());
    }

    public void saveModule(CourseModule module) {
        moduleRepo.save(module);
    }

    public CourseModule findModuleByIdAsInstructor(User instructor, UUID moduleId) {
        var module = findModuleById(instructor, moduleId);
        if (!module.getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, module);
        }
        return module;
    }

    public CourseModule findModuleById(User instructor, @NonNull UUID moduleId) {
        var module = moduleRepo.findById(moduleId)
            .orElseThrow(() -> new CourseModuleNotFoundException(moduleId));

        courseService.assertUserHasAccessToCourseContent(instructor, module.getCourse());
        return module;
    }
}
