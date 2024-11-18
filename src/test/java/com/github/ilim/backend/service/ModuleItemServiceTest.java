package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.enums.ModuleItemType;
import com.github.ilim.backend.exception.exceptions.ModuleItemNotFoundException;
import com.github.ilim.backend.repo.ModuleItemRepo;
import com.github.ilim.backend.repo.QuizRepo;
import com.github.ilim.backend.repo.VideoRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import({ModuleItemService.class})
public class ModuleItemServiceTest {

    @Autowired
    private ModuleItemService moduleItemService;

    @Autowired
    private ModuleItemRepo moduleItemRepo;

    @Autowired
    private VideoRepo videoRepo;

    @Autowired
    private QuizRepo quizRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private CourseModuleItem videoModuleItem;
    private CourseModuleItem quizModuleItem;
    private Video video;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        // Create a Video
        video = new Video();
        video.setId(UUID.randomUUID());
        video.setTitle("Test Video");
        video.setVideoUrl("http://example.com/video.mp4");
        video = videoRepo.save(video);

        // Create a Quiz
        quiz = new Quiz();
        quiz.setId(UUID.randomUUID());
        quiz.setTitle("Test Quiz");
        quiz.setPassingScore(BigDecimal.TEN);
        quiz = quizRepo.save(quiz);

        // Create ModuleItems
        videoModuleItem = new CourseModuleItem();
        videoModuleItem.setId(UUID.randomUUID());
        videoModuleItem.setItemType(ModuleItemType.VIDEO);
        videoModuleItem.setVideo(video);
        videoModuleItem = moduleItemRepo.save(videoModuleItem);

        quizModuleItem = new CourseModuleItem();
        quizModuleItem.setId(UUID.randomUUID());
        quizModuleItem.setItemType(ModuleItemType.QUIZ);
        quizModuleItem.setQuiz(quiz);
        quizModuleItem = moduleItemRepo.save(quizModuleItem);
    }

    @Test
    void testFindModuleItemByVideo_Success() {
        CourseModuleItem foundItem = moduleItemService.findModuleItemByVideo(video);

        assertNotNull(foundItem);
        assertEquals(videoModuleItem.getId(), foundItem.getId());
    }

    @Test
    void testFindModuleItemByVideo_NotFound() {
        Video nonExistentVideo = new Video();
        nonExistentVideo.setId(UUID.randomUUID());

        assertThrows(ModuleItemNotFoundException.class, () -> {
            moduleItemService.findModuleItemByVideo(nonExistentVideo);
        });
    }

    @Test
    void testFindModuleItemByQuiz_Success() {
        CourseModuleItem foundItem = moduleItemService.findModuleItemByQuiz(quiz);

        assertNotNull(foundItem);
        assertEquals(quizModuleItem.getId(), foundItem.getId());
    }

    @Test
    void testFindModuleItemByQuiz_NotFound() {
        Quiz nonExistentQuiz = new Quiz();
        nonExistentQuiz.setId(UUID.randomUUID());

        assertThrows(ModuleItemNotFoundException.class, () -> {
            moduleItemService.findModuleItemByQuiz(nonExistentQuiz);
        });
    }
}
