package com.github.ilim.backend.exception;

import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.exception.exceptions.ModuleItemNotFoundException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModuleItemExceptionHandlerTest {

    private ModuleItemExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ModuleItemExceptionHandler();
    }

    @Test
    void handleModuleItemNotFoundException_WithItemId() {
        UUID itemId = UUID.randomUUID();
        String expectedMessage = "CourseModuleItem(" + itemId + ") is not found";
        ModuleItemNotFoundException exception = new ModuleItemNotFoundException(itemId);
        ApiRes<Res<String>> response = handler.handleModuleItemNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleModuleItemNotFoundException_WithVideo() {
        UUID videoId = UUID.randomUUID();
        Video video = new Video();
        video.setId(videoId);
        String expectedMessage = "No CourseModuleItem found for Video(" + videoId + ")";
        ModuleItemNotFoundException exception = new ModuleItemNotFoundException(video);
        ApiRes<Res<String>> response = handler.handleModuleItemNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleModuleItemNotFoundException_WithQuiz() {
        UUID quizId = UUID.randomUUID();
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        String expectedMessage = "No CourseModuleItem found for Quiz(" + quizId + ")";
        ModuleItemNotFoundException exception = new ModuleItemNotFoundException(quiz);
        ApiRes<Res<String>> response = handler.handleModuleItemNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

}
