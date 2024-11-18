package com.github.ilim.backend.exception;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseModuleExceptionHandlerTest {

    private CourseModuleExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CourseModuleExceptionHandler();
    }

    @Test
    void handleCourseModuleNotFoundException() {
        UUID moduleId = UUID.randomUUID();
        String expectedMessage = "CourseModule(" + moduleId + ") is not found";
        CourseModuleNotFoundException exception = new CourseModuleNotFoundException(moduleId);
        ApiRes<Res<String>> response = handler.handleCourseModuleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleNotCourseInstructorException_WithCourse() {
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "User(null) is not the instructor of the course, and thus cannot submit, edit or delete Course(" + courseId + ")!";
        CourseModule module = new CourseModule();
        module.setId(UUID.randomUUID());
        Course course = new Course();
        course.setId(courseId);
        module.setCourse(course);
        NotCourseInstructorException exception = new NotCourseInstructorException(null, course);
        ApiRes<Res<String>> response = handler.handleNotCourseInstructorException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    // Additional test methods for other CourseModuleExceptionHandler exceptions can be added here
}
