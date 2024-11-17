package com.github.ilim.backend.entity;

import com.github.ilim.backend.enums.ModuleItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseModuleItemTest {

    @Test
    void testCreateWithVideo() {
        Video video = new Video();
        video.setTitle("Lesson 1");

        CourseModuleItem item = CourseModuleItem.create(video);

        assertEquals(ModuleItemType.VIDEO, item.getItemType());
        assertEquals(video, item.getVideo());
    }

    @Test
    void testCreateWithQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Quiz 1");

        CourseModuleItem item = CourseModuleItem.create(quiz);

        assertEquals(ModuleItemType.QUIZ, item.getItemType());
        assertEquals(quiz, item.getQuiz());
    }
}
