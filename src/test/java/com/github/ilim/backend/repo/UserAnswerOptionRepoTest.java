package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.UserAnswerOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserAnswerOptionRepoTest {

    @Autowired
    private UserAnswerOptionRepo userAnswerOptionRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void testSaveAndFindById() {
        UserAnswerOption option = new UserAnswerOption();
        userAnswerOptionRepo.save(option);

        UserAnswerOption foundOption = userAnswerOptionRepo.findById(option.getId()).orElse(null);

        assertNotNull(foundOption);
        assertEquals(option.getId(), foundOption.getId());
    }
}
