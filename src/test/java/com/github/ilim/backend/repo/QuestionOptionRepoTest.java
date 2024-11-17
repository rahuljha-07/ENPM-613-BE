package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.QuestionOption;
import com.github.ilim.backend.enums.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuestionOptionRepoTest {

    @Autowired
    private QuestionOptionRepo questionOptionRepo;

    @Autowired
    private QuestionRepo questionRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question();
        question.setText("What is Java?");
        question.setPoints(BigDecimal.TEN);
        question.setType(QuestionType.TRUE_FALSE);
        questionRepo.save(question);

        QuestionOption option1 = new QuestionOption();
        option1.setQuestion(question);
        option1.setText("Programming Language");
        option1.setIsCorrect(true);
        questionOptionRepo.save(option1);

        QuestionOption option2 = new QuestionOption();
        option2.setQuestion(question);
        option2.setText("Coffee");
        option2.setIsCorrect(false);
        questionOptionRepo.save(option2);
    }

    @Test
    void testFindByQuestionAndIsCorrect() {
        List<QuestionOption> correctOptions = questionOptionRepo.findByQuestionAndIsCorrect(question, true, Sort.unsorted());

        assertEquals(1, correctOptions.size());
        assertTrue(correctOptions.getFirst().getIsCorrect());
    }
}
