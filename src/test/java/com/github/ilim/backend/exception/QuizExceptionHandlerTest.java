package com.github.ilim.backend.exception;

import com.github.ilim.backend.exception.exceptions.CantAttemptOwnQuizException;
import com.github.ilim.backend.exception.exceptions.CantDeleteAttemptedQuizException;
import com.github.ilim.backend.exception.exceptions.MissingAnswerException;
import com.github.ilim.backend.exception.exceptions.QuizAttemptsNotFoundException;
import com.github.ilim.backend.exception.exceptions.QuizNotFoundException;
import com.github.ilim.backend.exception.exceptions.StudentDidNotCompleteCourseException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizExceptionHandlerTest {

    private QuizExceptionHandler quizHandler;
    private CourseExceptionHandler courseHandler;

    @BeforeEach
    void setUp() {
        quizHandler = new QuizExceptionHandler();
        courseHandler = new CourseExceptionHandler();
    }

    @Test
    void handleCantAttemptOwnQuizException() {
        String userId = "user-206";
        UUID quizId = UUID.randomUUID();
        String expectedMessage = "User(" + userId + ") can't attempt Quiz(" + quizId + ") because user is the course instructor and creator of the quiz";
        CantAttemptOwnQuizException exception = new CantAttemptOwnQuizException(userId, quizId);
        ApiRes<Res<String>> response = quizHandler.handleCantAttemptOwnQuizException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCantDeleteAttemptedQuizException() {
        String instructorId = "instructor-301";
        UUID quizId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        String expectedMessage = "User(" + instructorId + ") cannot delete Quiz(" + quizId + ") because some student attempted it in QuizAttemptId(" + attemptId + ")";
        CantDeleteAttemptedQuizException exception = new CantDeleteAttemptedQuizException(instructorId, quizId, attemptId);
        ApiRes<Res<String>> response = quizHandler.handleCantDeleteAttemptedQuizException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleQuizAttemptsNotFoundException() {
        UUID quizId = UUID.randomUUID();
        String expectedMessage = "Couldn't find any quiz attempt for Quiz(" + quizId + ")!";
        QuizAttemptsNotFoundException exception = new QuizAttemptsNotFoundException(quizId);
        ApiRes<Res<String>> response = quizHandler.handleQuizAttemptNoteFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleQuizNotFoundException() {
        UUID quizId = UUID.randomUUID();
        String expectedMessage = "Quiz(" + quizId + ") is not found";
        QuizNotFoundException exception = new QuizNotFoundException(quizId);
        ApiRes<Res<String>> response = quizHandler.handleQuizNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleMissingAnswerException() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String expectedMessage = "In attempt(" + attemptId + ") doesn't have an answer for Question(" + questionId + ")!";
        MissingAnswerException exception = new MissingAnswerException(attemptId, questionId);
        ApiRes<Res<String>> response = quizHandler.handleMissingAnswerException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleStudentDidNotCompleteCourseException() {
        String studentId = "student-303";
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "Student(" + studentId + ") tried to do an operations on Course(" + courseId + ") without completing all quizzes first!";
        StudentDidNotCompleteCourseException exception = new StudentDidNotCompleteCourseException(studentId, courseId);
        ApiRes<Res<String>> response = courseHandler.handleStudentDidNotCompleteCourseException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }
}
