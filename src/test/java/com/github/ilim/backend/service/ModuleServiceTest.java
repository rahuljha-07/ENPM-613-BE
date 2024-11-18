package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.ModuleDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.repo.ModuleRepo;
import com.github.ilim.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import({ModuleService.class})
public class ModuleServiceTest {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ModuleRepo moduleRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private UserRepo userRepo;

    // Mock dependencies
    @MockBean
    private CourseService courseService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User instructor;
    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        // Create an instructor
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString());
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setBirthdate(LocalDateTime.now().toLocalDate());
        instructor = userRepo.save(instructor);

        // Create a student
        student = new User();
        student.setId(UUID.randomUUID().toString());
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setRole(UserRole.STUDENT);
        student.setBirthdate(LocalDateTime.now().toLocalDate());
        student = userRepo.save(student);

        // Create a course
        course = new Course();
        course.setTitle("Test Course");
        course.setPrice(new BigDecimal("49.99"));
        course.setInstructor(instructor);
        course = courseRepo.save(course);
    }

    @Test
    @Transactional
    void testAddModuleToCourse_Success() {
        UUID courseId = course.getId();

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Module 1");
        moduleDto.setDescription("Module Description");

        when(courseService.findCourseByIdAndUser(instructor, courseId)).thenReturn(course);

        moduleService.addModuleToCourse(instructor, courseId, moduleDto);

        Course updatedCourse = courseRepo.findById(courseId).orElse(null);
        assertNotNull(updatedCourse);
        assertEquals(1, updatedCourse.getCourseModules().size());
        CourseModule module = updatedCourse.getCourseModules().get(0);
        assertEquals("Module 1", module.getTitle());
        assertEquals("Module Description", module.getDescription());
    }

    @Test
    void testAddModuleToCourse_NotInstructor() {
        UUID courseId = course.getId();

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Module 1");
        moduleDto.setDescription("Module Description");

        when(courseService.findCourseByIdAndUser(student, courseId))
            .thenThrow(new UserHasNoAccessToCourseException(student, courseId));

        assertThrows(UserHasNoAccessToCourseException.class, () -> {
            moduleService.addModuleToCourse(student, courseId, moduleDto);
        });
    }

    @Test
    @Transactional
    void testDeleteCourseModule_Success() {
        // Add a module to the course
        CourseModule module = new CourseModule();
        module.setTitle("Module 1");
        module.setCourse(course);
        module = moduleRepo.save(module);
        course.getCourseModules().add(module);
        courseRepo.save(course);

        //when(moduleService.findModuleByIdAsInstructor(instructor, module.getId())).thenReturn(module);
        moduleService.findModuleByIdAsInstructor(instructor, module.getId());


        moduleService.deleteCourseModule(instructor, module.getId());

        Course updatedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(updatedCourse);
        assertTrue(updatedCourse.getCourseModules().isEmpty());
    }

    @Test
    void testDeleteCourseModule_NotInstructor() {
        // Add a module to the course
        CourseModule module = new CourseModule();
        module.setTitle("Module 1");
        module.setCourse(course);
        module = moduleRepo.save(module);
        course.getCourseModules().add(module);
        courseRepo.save(course);

        when(moduleService.findModuleByIdAsInstructor(instructor, module.getId()))
            .thenThrow(new NotCourseInstructorException(student, module));

        CourseModule finalModule = module;
        assertThrows(NotCourseInstructorException.class, () -> {
            moduleService.deleteCourseModule(student, finalModule.getId());
        });
    }

    @Test
    @Transactional
    void testUpdateCourseModule_Success() {
        // Add a module to the course
        CourseModule module = new CourseModule();
        module.setTitle("Module 1");
        module.setCourse(course);
        module = moduleRepo.save(module);
        course.getCourseModules().add(module);
        courseRepo.save(course);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setTitle("Updated Module Title");
        moduleDto.setDescription("Updated Description");

        moduleService.findModuleByIdAsInstructor(instructor, module.getId());

        moduleService.updateCourseModule(instructor, module.getId(), moduleDto);

        CourseModule updatedModule = moduleRepo.findById(module.getId()).orElse(null);
        assertNotNull(updatedModule);
        assertEquals("Updated Module Title", updatedModule.getTitle());
        assertEquals("Updated Description", updatedModule.getDescription());
    }

}
