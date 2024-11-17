package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.enums.QuestionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuestionRepoTest {

    @Autowired
    private QuestionRepo questionRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void testSaveAndFindById() {
        Question question = new Question();
        question.setText("What is Java?");
        question.setPoints(BigDecimal.TEN);
        question.setType(QuestionType.TRUE_FALSE);
        questionRepo.save(question);

        Question foundQuestion = questionRepo.findById(question.getId()).orElse(null);

        assertNotNull(foundQuestion);
        assertEquals(question.getId(), foundQuestion.getId());
    }
}
