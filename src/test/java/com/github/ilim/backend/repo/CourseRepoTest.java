package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepoTest {

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private UserRepo userRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User instructor;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString()); // Ensure ID is a String if that's the entity's type
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setBirthdate(LocalDate.now());
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor = userRepo.save(instructor);

        course1 = new Course();
        course1.setTitle("Course 1");
        course1.setPrice(new BigDecimal("100"));
        course1.setStatus(CourseStatus.PUBLISHED);
        course1.setDeleted(false);
        course1.setInstructor(instructor);
        courseRepo.save(course1);

        course2 = new Course();
        course2.setTitle("Course 2");
        course2.setPrice(new BigDecimal("200"));
        course2.setStatus(CourseStatus.DRAFT);
        course2.setDeleted(false);
        course2.setInstructor(instructor);
        courseRepo.save(course2);
    }

    @Test
    void testFindByIdAndStatusAndIsDeleted() {
        Optional<Course> foundCourse = courseRepo.findByIdAndStatusAndIsDeleted(course1.getId(), CourseStatus.PUBLISHED, false);

        assertTrue(foundCourse.isPresent());
        assertEquals(course1.getId(), foundCourse.get().getId());
    }

    @Test
    void testFindAllByStatusAndIsDeleted() {
        List<Course> courses = courseRepo.findAllByStatusAndIsDeleted(CourseStatus.PUBLISHED, false, Sort.unsorted());

        assertEquals(1, courses.size());
        assertEquals(course1.getId(), courses.getFirst().getId());
    }

    @Test
    void testFindAllByIdInAndIsDeleted() {
        List<Course> courses = courseRepo.findAllByIdInAndIsDeleted(
            Arrays.asList(course1.getId(), course2.getId()), false);

        assertEquals(2, courses.size());
    }

    @Test
    void testFindAllByInstructorAndIsDeleted() {
        List<Course> courses = courseRepo.findAllByInstructorAndIsDeleted(instructor, false, Sort.unsorted());

        assertEquals(2, courses.size());
    }
}
