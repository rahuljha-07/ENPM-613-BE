package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModuleItemNotFoundExceptionTest {

    @Test
    void testConstructorWithItemId() {
        UUID itemId = UUID.randomUUID();
        ModuleItemNotFoundException exception = new ModuleItemNotFoundException(itemId);
        assertEquals("CourseModuleItem() is not found".replace("()", "(" + itemId + ")"), exception.getMessage());
    }

    @Test
    void testConstructorWithVideo() {
        Video video = new Video();
        UUID videoId = UUID.randomUUID();
        video.setId(videoId);
        ModuleItemNotFoundException exception = new ModuleItemNotFoundException(video);
        assertEquals("No CourseModuleItem found for Video(" + videoId + ")", exception.getMessage());
    }

    @Test
    void testConstructorWithQuiz() {
        Quiz quiz = new Quiz();
        UUID quizId = UUID.randomUUID();
        quiz.setId(quizId);
        ModuleItemNotFoundException exception = new ModuleItemNotFoundException(quiz);
        assertEquals("No CourseModuleItem found for Quiz(" + quizId + ")", exception.getMessage());
    }
}
