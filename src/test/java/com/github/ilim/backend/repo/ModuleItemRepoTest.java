package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ModuleItemRepoTest {

    @Autowired
    private ModuleItemRepo moduleItemRepo;

    @Autowired
    private VideoRepo videoRepo;

    @Autowired
    private QuizRepo quizRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private Video video;
    private Quiz quiz;
    private CourseModuleItem videoItem;
    private CourseModuleItem quizItem;

    @BeforeEach
    void setUp() {
        video = new Video();
        video.setTitle("Video 1");
        video.setVideoUrl("http://example.com/video.mp4");
        videoRepo.save(video);

        quiz = new Quiz();
        quiz.setTitle("Quiz 1");
        quiz.setPassingScore(BigDecimal.TEN);
        quizRepo.save(quiz);

        videoItem = CourseModuleItem.create(video);
        moduleItemRepo.save(videoItem);

        quizItem = CourseModuleItem.create(quiz);
        moduleItemRepo.save(quizItem);
    }

    @Test
    void testFindByVideo() {
        Optional<CourseModuleItem> foundItem = moduleItemRepo.findByVideo(video);

        assertTrue(foundItem.isPresent());
        assertEquals(videoItem.getId(), foundItem.get().getId());
    }

    @Test
    void testFindByQuiz() {
        Optional<CourseModuleItem> foundItem = moduleItemRepo.findByQuiz(quiz);

        assertTrue(foundItem.isPresent());
        assertEquals(quizItem.getId(), foundItem.get().getId());
    }
}
