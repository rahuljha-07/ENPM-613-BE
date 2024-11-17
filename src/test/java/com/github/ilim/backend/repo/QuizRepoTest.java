package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuizRepoTest {

    @Autowired
    private QuizRepo quizRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void testSaveAndFindById() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Quiz 1");
        quiz.setPassingScore(BigDecimal.TEN);
        quizRepo.save(quiz);

        Quiz foundQuiz = quizRepo.findById(quiz.getId()).orElse(null);

        assertNotNull(foundQuiz);
        assertEquals(quiz.getId(), foundQuiz.getId());
    }
}
