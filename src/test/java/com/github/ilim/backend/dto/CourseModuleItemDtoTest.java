package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.enums.ModuleItemType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseModuleItemDtoTest {

    @Test
    void testFrom_WithQuiz_Success() {
        // Arrange
        UUID courseModuleItemId = UUID.randomUUID();
        UUID courseModuleId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();
        int orderIndex = 1;

        // Create CourseModule
        CourseModule courseModule = new CourseModule();
        courseModule.setId(courseModuleId);
        courseModule.setTitle("Module 1");
        courseModule.setOrderIndex(1);

        // Create Quiz
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Sample Quiz");
        quiz.setCourseModule(courseModule);
        quiz.setQuestions(List.of());

        // Create CourseModuleItem
        CourseModuleItem courseModuleItem = new CourseModuleItem();
        courseModuleItem.setId(courseModuleItemId);
        courseModuleItem.setCourseModule(courseModule);
        courseModuleItem.setQuiz(quiz);
        courseModuleItem.setItemType(ModuleItemType.QUIZ);
        courseModuleItem.setOrderIndex(orderIndex);
        courseModuleItem.setQuiz(quiz);

        // Act
        CourseModuleItemDto dto = CourseModuleItemDto.from(courseModuleItem);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(courseModuleItemId, dto.getId(), "ID should match");
        assertEquals(courseModuleId, dto.getCourseModuleId(), "CourseModule ID should match");
        assertEquals(quizId, dto.getItemId(), "Item ID should match");
        assertEquals(ModuleItemType.QUIZ, dto.getItemType(), "Item type should be QUIZ");
        assertEquals(orderIndex, dto.getOrderIndex(), "Order index should match");
        assertNotNull(dto.getPayload(), "Payload should not be null");
        assertInstanceOf(StudentQuizDto.class, dto.getPayload(), "Payload should be an instance of StudentQuizDto");

        StudentQuizDto quizDto = (StudentQuizDto) dto.getPayload();
        assertEquals(quizId, quizDto.id(), "Quiz DTO ID should match");
        assertEquals("Sample Quiz", quizDto.title(), "Quiz DTO title should match");
    }

    @Test
    void testFrom_WithVideo_Success() {
        // Arrange
        UUID courseModuleItemId = UUID.randomUUID();
        UUID courseModuleId = UUID.randomUUID();
        UUID videoId = UUID.randomUUID();
        int orderIndex = 2;

        // Create CourseModule
        CourseModule courseModule = new CourseModule();
        courseModule.setId(courseModuleId);
        courseModule.setTitle("Module 2");
        courseModule.setOrderIndex(2);

        // Create Video
        Video video = new Video();
        video.setId(videoId);
        video.setTitle("Sample Video");
        video.setVideoUrl("http://example.com/video");

        // Create CourseModuleItem
        CourseModuleItem courseModuleItem = new CourseModuleItem();
        courseModuleItem.setId(courseModuleItemId);
        courseModuleItem.setCourseModule(courseModule);
        courseModuleItem.setItemType(ModuleItemType.VIDEO);
        courseModuleItem.setOrderIndex(orderIndex);
        courseModuleItem.setVideo(video);

        // Act
        CourseModuleItemDto dto = CourseModuleItemDto.from(courseModuleItem);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(courseModuleItemId, dto.getId(), "ID should match");
        assertEquals(courseModuleId, dto.getCourseModuleId(), "CourseModule ID should match");
        assertEquals(ModuleItemType.VIDEO, dto.getItemType(), "Item type should be VIDEO");
        assertEquals(videoId, dto.getItemId(), "Item ID should match");
        assertEquals(orderIndex, dto.getOrderIndex(), "Order index should match");
        assertNotNull(dto.getPayload(), "Payload should not be null");
        assertInstanceOf(VideoDto.class, dto.getPayload(), "Payload should be an instance of VideoDto");

        VideoDto videoDto = (VideoDto) dto.getPayload();
        assertEquals("Sample Video", videoDto.getTitle(), "Video DTO title should match");
        assertEquals("http://example.com/video", videoDto.getVideoUrl(), "Video DTO URL should match");
    }

    @Test
    void testFrom_NullCourseModule_ThrowsException() {
        // Arrange
        UUID courseModuleItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        int orderIndex = 4;

        // Create CourseModuleItem with null CourseModule
        CourseModuleItem courseModuleItem = new CourseModuleItem();
        courseModuleItem.setId(courseModuleItemId);
        courseModuleItem.setCourseModule(null); // Null CourseModule
        courseModuleItem.setItemType(ModuleItemType.QUIZ);
        courseModuleItem.setOrderIndex(orderIndex);

        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            CourseModuleItemDto.from(courseModuleItem);
        }, "Expected NullPointerException when CourseModule is null");

        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }

    @Test
    void testFrom_NullCourseModuleItem_ThrowsException() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            CourseModuleItemDto.from(null);
        }, "Expected NullPointerException when CourseModuleItem is null");

        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }
}
