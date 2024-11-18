package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotCourseInstructorExceptionTest {

    private static String getMessage(User user, String itemName, UUID itemId) {
        return "User(" + (user != null ? user.getId() : "null") + ") is not the instructor of the course, and thus cannot submit, edit or delete " + itemName + "(" + itemId + ")!";
    }

    @Test
    void testConstructorWithCourse() {
        User user = new User();
        user.setId("user-777");
        Course course = new Course();
        UUID courseId = UUID.randomUUID();
        course.setId(courseId);
        NotCourseInstructorException exception = new NotCourseInstructorException(user, course);
        assertEquals(getMessage(user, "Course", courseId), exception.getMessage());
    }

    @Test
    void testConstructorWithCourseModule() {
        User user = new User();
        user.setId("user-888");
        CourseModule module = new CourseModule();
        UUID moduleId = UUID.randomUUID();
        module.setId(moduleId);
        NotCourseInstructorException exception = new NotCourseInstructorException(user, module);
        assertEquals(getMessage(user, "CourseModule", moduleId), exception.getMessage());
    }

    @Test
    void testConstructorWithVideo() {
        User user = new User();
        user.setId("user-999");
        Video video = new Video();
        UUID videoId = UUID.randomUUID();
        video.setId(videoId);
        NotCourseInstructorException exception = new NotCourseInstructorException(user, video);
        assertEquals(getMessage(user, "Video", videoId), exception.getMessage());
    }

    @Test
    void testConstructorWithQuiz() {
        User user = new User();
        user.setId("user-1010");
        Quiz quiz = new Quiz();
        UUID quizId = UUID.randomUUID();
        quiz.setId(quizId);
        NotCourseInstructorException exception = new NotCourseInstructorException(user, quiz);
        assertEquals(getMessage(user, "Quiz", quizId), exception.getMessage());
    }
}
