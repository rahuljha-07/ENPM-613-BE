package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.UserAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserAnswerRepoTest {

    @Autowired
    private UserAnswerRepo userAnswerRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void testSaveAndFindById() {
        UserAnswer answer = new UserAnswer();
        userAnswerRepo.save(answer);

        UserAnswer foundAnswer = userAnswerRepo.findById(answer.getId()).orElse(null);

        assertNotNull(foundAnswer);
        assertEquals(answer.getId(), foundAnswer.getId());
    }
}
