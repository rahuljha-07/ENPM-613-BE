package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.dto.CourseRejectionDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.CourseAlreadyPublished;
import com.github.ilim.backend.exception.exceptions.CourseIsNotWaitingApprovalException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import({CourseService.class})
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CoursePurchaseRepo coursePurchaseRepo;

    // Mock dependencies
    @MockBean
    private UserService userService;

    @MockBean
    private CoursePurchaseService coursePurchaseService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User instructor;
    private User admin;
    private User student;
    private Course course;
    @Autowired
    private ModuleRepo moduleRepo;

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

        // Create an admin
        admin = new User();
        admin.setId(UUID.randomUUID().toString());
        admin.setEmail("admin@example.com");
        admin.setName("Admin Name");
        admin.setRole(UserRole.ADMIN);
        admin.setBirthdate(LocalDateTime.now().toLocalDate());
        admin = userRepo.save(admin);

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
        course.setStatus(CourseStatus.DRAFT);
        course.setInstructor(instructor);
        course = courseRepo.save(course);
    }

    @Test
    @Transactional
    void testCreateCourse_Success() {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("New Course");
        courseDto.setPrice(new BigDecimal("59.99"));
        // Populate other necessary fields

        Course newCourse = courseService.create(instructor, courseDto);

        assertNotNull(newCourse);
        assertEquals("New Course", newCourse.getTitle());
        assertEquals(new BigDecimal("59.99"), newCourse.getPrice());
        assertEquals(instructor.getId(), newCourse.getInstructor().getId());
        assertEquals(CourseStatus.DRAFT, newCourse.getStatus());
    }

    @Test
    void testCreateCourse_UserNotInstructor() {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("New Course");
        courseDto.setPrice(new BigDecimal("59.99"));

        // Attempt to create course as student
        assertThrows(UserCannotCreateCourseException.class, () -> {
            courseService.create(student, courseDto);
        });
    }

    @Test
    @Transactional
    void testApproveCourse_Success() {
        // Set course status to WAIT_APPROVAL
        course.setStatus(CourseStatus.WAIT_APPROVAL);
        courseRepo.save(course);

        courseService.approveCourse(admin, course.getId());

        Course updatedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(updatedCourse);
        assertEquals(CourseStatus.PUBLISHED, updatedCourse.getStatus());
    }

    @Test
    void testApproveCourse_AlreadyPublished() {
        // Set course status to PUBLISHED
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepo.save(course);

        assertThrows(CourseAlreadyPublished.class, () -> {
            courseService.approveCourse(admin, course.getId());
        });
    }

    @Test
    @Transactional
    void testRejectCourse_Success() {
        // Set course status to WAIT_APPROVAL
        course.setStatus(CourseStatus.WAIT_APPROVAL);
        courseRepo.save(course);

        CourseRejectionDto rejectionDto = new CourseRejectionDto();
        rejectionDto.setReason("Insufficient content");

        courseService.rejectCourse(admin, course.getId(), rejectionDto);

        Course updatedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(updatedCourse);
        assertEquals(CourseStatus.DRAFT, updatedCourse.getStatus());
        // Note: The reason could be used for notification purposes
    }

    @Test
    void testRejectCourse_NotWaitingApproval() {
        // Course status is DRAFT
        assertThrows(CourseIsNotWaitingApprovalException.class, () -> {
            CourseRejectionDto rejectionDto = new CourseRejectionDto();
            rejectionDto.setReason("Insufficient content");

            courseService.rejectCourse(admin, course.getId(), rejectionDto);
        });
    }

    @Test
    @Transactional
    void testUpdateCourse_Success() {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Updated Course Title");
        courseDto.setPrice(new BigDecimal("69.99"));
        // Populate other necessary fields

        courseService.updateCourse(instructor, course.getId(), courseDto);

        Course updatedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(updatedCourse);
        assertEquals("Updated Course Title", updatedCourse.getTitle());
        assertEquals(new BigDecimal("69.99"), updatedCourse.getPrice());
    }

    @Test
    void testUpdateCourse_NotInstructor() {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Updated Course Title");
        courseDto.setPrice(new BigDecimal("69.99"));

        // Attempt to update course as student
        assertThrows(UserHasNoAccessToCourseException.class, () -> {
            courseService.updateCourse(student, course.getId(), courseDto);
        });
    }

    @Test
    @Transactional
    void testFindPublishedCourse_Success() {
        // Set course status to PUBLISHED
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepo.save(course);

        Course foundCourse = courseService.findPublishedCourse(course.getId());

        assertNotNull(foundCourse);
        assertEquals(CourseStatus.PUBLISHED, foundCourse.getStatus());
    }

    @Test
    void testFindPublishedCourse_NotFound() {
        UUID nonExistentCourseId = UUID.randomUUID();

        assertThrows(CourseNotFoundException.class, () -> {
            courseService.findPublishedCourse(nonExistentCourseId);
        });
    }

    @Test
    @Transactional
    void testDeleteCourseAsAdmin_Success() {
        courseService.deleteCourseAsAdmin(course.getId());

        Course deletedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(deletedCourse);
        assertTrue(deletedCourse.isDeleted());
    }

    @Test
    void testDeleteCourseAsAdmin_NotFound() {
        UUID nonExistentCourseId = UUID.randomUUID();

        assertThrows(CourseNotFoundException.class, () -> {
            courseService.deleteCourseAsAdmin(nonExistentCourseId);
        });
    }
    @Test
    @Transactional
    void testSubmitCourseForApproval_Success() {
        // Set course to DRAFT status
        course.setStatus(CourseStatus.DRAFT);
        courseRepo.save(course);

        courseService.submitCourseForApproval(instructor, course.getId());

        Course updatedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(updatedCourse);
        assertEquals(CourseStatus.WAIT_APPROVAL, updatedCourse.getStatus());
    }

    @Test
    void testSubmitCourseForApproval_NotInstructor() {
        assertThrows(UserHasNoAccessToCourseException.class, () -> {
            courseService.submitCourseForApproval(student, course.getId());
        });
    }

    @Test
    void testSubmitCourseForApproval_CourseNotFound() {
        UUID nonExistentCourseId = UUID.randomUUID();
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.submitCourseForApproval(instructor, nonExistentCourseId);
        });
    }

    @Test
    void testFindCourseByIdAndUser_StudentNoAccess() {
        assertThrows(UserHasNoAccessToCourseException.class, () -> {
            courseService.findCourseByIdAndUser(student, course.getId());
        });
    }

    @Test
    void testFindCourseByIdAndUser_InstructorAccessOwnCourse() {
        Course foundCourse = courseService.findCourseByIdAndUser(instructor, course.getId());
        assertNotNull(foundCourse);
    }

    @Test
    void testFindCourseByIdAndUser_AdminAccessAnyCourse() {
        Course foundCourse = courseService.findCourseByIdAndUser(admin, course.getId());
        assertNotNull(foundCourse);
    }

    @Test
    @Transactional
    void testReorderCourseModules_Success() {
        // Create modules
        CourseModule module1 = new CourseModule();
        module1.setTitle("Module 1");
        module1.setOrderIndex(1);
        module1.setCourse(course);
        module1 = moduleRepo.save(module1);

        CourseModule module2 = new CourseModule();
        module2.setTitle("Module 2");
        module2.setOrderIndex(2);
        module2.setCourse(course);
        module2 = moduleRepo.save(module2);

        course.getCourseModules().addAll(List.of(module1, module2));
        courseRepo.save(course);

        // New order
        List<UUID> newOrder = List.of(module2.getId(), module1.getId());

        courseService.reorderCourseModules(instructor, course.getId(), newOrder);

        // Verify order
        Course updatedCourse = courseRepo.findById(course.getId()).orElse(null);
        assertNotNull(updatedCourse);
        List<CourseModule> modules = updatedCourse.getCourseModules();
        assertEquals(2, modules.size());
        assertEquals(module2.getId(), modules.get(0).getId());
        assertEquals(2, modules.get(0).getOrderIndex());
        assertEquals(module1.getId(), modules.get(1).getId());
        assertEquals(1, modules.get(1).getOrderIndex());
    }

    @Test
    void testReorderCourseModules_NotInstructor() {
        assertThrows(UserHasNoAccessToCourseException.class, () -> {
            courseService.reorderCourseModules(student, course.getId(), List.of());
        });
    }

    @Test
    void testReorderCourseModules_InvalidModuleIds() {
        List<UUID> invalidOrder = List.of(UUID.randomUUID());
        assertThrows(BadRequestException.class, () -> {
            courseService.reorderCourseModules(instructor, course.getId(), invalidOrder);
        });
    }

    @Test
    void testFindAllCourses_AsAdmin() {
        List<Course> courses = courseService.findAllCourses(admin);
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
    }
    @Test
    void testFindCoursesWaitingForApproval_AsAdmin() {
        course.setStatus(CourseStatus.WAIT_APPROVAL);
        courseRepo.save(course);

        List<Course> courses = courseService.findCoursesWaitingForApproval(admin);
        assertNotNull(courses);
        assertTrue(courses.contains(course));
    }

    @Test
    void testUpdateCourse_CourseNotFound() {
        UUID nonExistentCourseId = UUID.randomUUID();
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("Updated Title");

        assertThrows(CourseNotFoundException.class, () -> {
            courseService.updateCourse(instructor, nonExistentCourseId, courseDto);
        });
    }

}
