package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.ModuleDto;
import com.github.ilim.backend.dto.StudentCourseModuleDto;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.repo.ModuleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class responsible for managing course modules.
 * <p>
 * Provides functionalities such as adding, deleting, and updating course modules,
 * as well as enforcing access controls based on user roles.
 * </p>
 *
 * @see ModuleRepo
 * @see CourseService
 */
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepo moduleRepo;
    private final CourseService courseService;

    /**
     * Adds a new module to a specified course.
     * <p>
     * Associates the provided {@link ModuleDto} with the course identified by {@code courseId} and saves the updated course.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor adding the module
     * @param courseId   the unique identifier of the course to which the module will be added
     * @param moduleDto  the data transfer object containing module details
     */
    @Transactional
    public void addModuleToCourse(User instructor, UUID courseId, ModuleDto moduleDto) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = CourseModule.from(moduleDto);
        course.addCourseModule(module);
        courseService.saveCourse(course);
    }

    /**
     * Deletes a module from a specified course.
     * <p>
     * Removes the module identified by {@code moduleId} from the course and saves the updated course.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor deleting the module
     * @param moduleId   the unique identifier of the module to be deleted
     * @throws CourseModuleNotFoundException if the module is not found within the course
     * @throws NotCourseInstructorException  if the user is not the instructor of the course
     */
    @Transactional
    public void deleteCourseModule(User instructor, UUID moduleId) {
        var module = findModuleByIdAsInstructor(instructor, moduleId);
        var course = module.getCourse();
        course.deleteCourseModule(module);
        courseService.saveCourse(course);
    }

    /**
     * Updates the details of a specified course module.
     * <p>
     * Updates the module identified by {@code moduleId} with the provided {@link ModuleDto} and saves the updated course.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor updating the module
     * @param moduleId   the unique identifier of the module to be updated
     * @param dto        the data transfer object containing updated module details
     * @throws CourseModuleNotFoundException if the module is not found within the course
     * @throws NotCourseInstructorException  if the user is not the instructor of the course
     */
    @Transactional
    public void updateCourseModule(User instructor, UUID moduleId, ModuleDto dto) {
        var module = findModuleByIdAsInstructor(instructor, moduleId);
        module.updateFrom(dto);
        courseService.saveCourse(module.getCourse());
    }

    /**
     * Saves a {@link CourseModule} entity to the repository.
     *
     * @param module the {@link CourseModule} entity to be saved
     */
    @Transactional
    public void saveModule(CourseModule module) {
        moduleRepo.save(module);
    }

    /**
     * Retrieves a course module by its unique identifier, ensuring that the requesting instructor is authorized.
     *
     * @param instructor the {@link User} entity representing the instructor requesting the module
     * @param moduleId   the unique identifier of the module to retrieve
     * @return the {@link CourseModule} entity corresponding to the provided ID
     * @throws CourseModuleNotFoundException if the module is not found within the course
     * @throws NotCourseInstructorException  if the user is not the instructor of the course
     */
    public CourseModule findModuleByIdAsInstructor(User instructor, UUID moduleId) {
        var module = findById(instructor, moduleId);
        if (!module.getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, module);
        }
        return module;
    }

    /**
     * Retrieves a course module by its unique identifier and enforces access controls.
     *
     * @param instructor the {@link User} entity representing the instructor requesting the module
     * @param moduleId   the unique identifier of the module to retrieve
     * @return the {@link CourseModule} entity or a {@link StudentCourseModuleDto} based on access permissions
     */
    public Object findCourseModuleById(User instructor, UUID moduleId) {
        var module = findById(instructor, moduleId);
        if (module.getCourse().getInstructor().equals(instructor) || instructor.getRole() == UserRole.ADMIN) {
            return module;
        }
        return StudentCourseModuleDto.from(module);
    }

    /**
     * Finds a course module by its ID and enforces user access to course content.
     *
     * @param student  the {@link User} entity requesting access
     * @param moduleId the unique identifier of the module to retrieve
     * @return the {@link CourseModule} entity
     * @throws CourseModuleNotFoundException if the module is not found
     */
    private CourseModule findById(User student, @NonNull UUID moduleId) {
        var module = moduleRepo.findById(moduleId)
            .orElseThrow(() -> new CourseModuleNotFoundException(moduleId));
        courseService.assertUserHasAccessToCourseContent(student, module.getCourse());
        return module;
    }
}
