package com.github.ilim.backend.exception;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.exception.exceptions.AccessDeletedCourseException;
import com.github.ilim.backend.exception.exceptions.CantCreatePublicCourseException;
import com.github.ilim.backend.exception.exceptions.CantGenerateCertificateException;
import com.github.ilim.backend.exception.exceptions.CourseAlreadyPublished;
import com.github.ilim.backend.exception.exceptions.CourseIsNotWaitingApprovalException;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.CourseStatusNotDraftException;
import com.github.ilim.backend.exception.exceptions.NoAccessToCourseContentException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseExceptionHandlerTest {

    private CourseExceptionHandler courseHandler;
    private CourseModuleExceptionHandler moduleHandler;
    private ModuleItemExceptionHandler itemHandler;

    @BeforeEach
    void setUp() {
        courseHandler = new CourseExceptionHandler();
        moduleHandler = new CourseModuleExceptionHandler();
        itemHandler = new ModuleItemExceptionHandler();
    }

    @Test
    void handleAccessDeletedCourseException() {
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "You can't access a deleted course(" + courseId + ")";
        AccessDeletedCourseException exception = new AccessDeletedCourseException(courseId);
        ApiRes<Res<String>> response = courseHandler.handleAccessDeletedCourseException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCourseAlreadyPublished() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setId(courseId);
        String expectedMessage = "Course(" + courseId + ") already published";
        CourseAlreadyPublished exception = new CourseAlreadyPublished(course);
        ApiRes<Res<String>> response = courseHandler.handleCourseAlreadyPublished(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCourseIsNotWaitingApprovalException() {
        UUID courseId = UUID.randomUUID();
        CourseStatus status = CourseStatus.PUBLISHED;
        String expectedMessage = "Course(" + courseId + ") is not in a WAIT_APPROVAL status, it is " + status;
        CourseIsNotWaitingApprovalException exception = new CourseIsNotWaitingApprovalException(courseId, status);
        ApiRes<Res<String>> response = courseHandler.handleCourseIsNotWaitingApprovalException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCourseModuleNotFoundException() {
        UUID moduleId = UUID.randomUUID();
        String expectedMessage = "CourseModule(" + moduleId + ") is not found";
        CourseModuleNotFoundException exception = new CourseModuleNotFoundException(moduleId);
        ApiRes<Res<String>> response = moduleHandler.handleCourseModuleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCourseNotFoundException() {
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "Course with id '" + courseId + "' not found";
        CourseNotFoundException exception = new CourseNotFoundException(courseId);
        ApiRes<Res<String>> response = courseHandler.handleCourseNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCourseStatusNotDraftException() {
        UUID courseId = UUID.randomUUID();
        CourseStatus currentStatus = CourseStatus.PUBLISHED;
        Course course = new Course();
        course.setId(courseId);
        course.setStatus(currentStatus);
        String expectedMessage = "Course(" + courseId + ") must be in DRAFT status, not status=" + currentStatus.name();
        CourseStatusNotDraftException exception = new CourseStatusNotDraftException(course);
        ApiRes<Res<String>> response = courseHandler.handleCourseStatusNotDraftException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCantCreatePublicCourseException() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setId(courseId);
        course.setDeleted(false);
        course.setStatus(CourseStatus.PUBLISHED);
        String expectedMessage = "Cannot create a PublicCourseDto from a unknown reason Course(" + courseId + ")";
        CantCreatePublicCourseException exception = new CantCreatePublicCourseException(course);
        ApiRes<Res<String>> response = courseHandler.handleCantCreatePublicCourseException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleNoAccessToCourseContentException() {
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "User(null) cannot access the content of the course(" + courseId + ")";
        NoAccessToCourseContentException exception = new NoAccessToCourseContentException(null, courseId);
        ApiRes<Res<String>> response = courseHandler.handleNoAccessToCourseContentException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCantGenerateCertificateException() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setId(courseId);
        String expectedMessage = "Failed to generate a certificate for Course(" + courseId + ")";
        CantGenerateCertificateException exception = new CantGenerateCertificateException(course, new Exception("Generation Error"));
        ApiRes<Res<String>> response = itemHandler.handleCantGenerateCertificateException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleNotCourseInstructorException() {
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "User(null) is not the instructor of the course, and thus cannot submit, edit or delete Course(" + courseId + ")!";
        Course course = new Course();
        course.setId(courseId);
        NotCourseInstructorException exception = new NotCourseInstructorException(null, course);
        ApiRes<Res<String>> response = moduleHandler.handleNotCourseInstructorException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }
}
