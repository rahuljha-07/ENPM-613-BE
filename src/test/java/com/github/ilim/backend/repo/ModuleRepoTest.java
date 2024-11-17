package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.CourseModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ModuleRepoTest {

    @Autowired
    private ModuleRepo moduleRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void testSaveAndFindById() {
        CourseModule module = new CourseModule();
        module.setTitle("Module 1");
        moduleRepo.save(module);

        CourseModule foundModule = moduleRepo.findById(module.getId()).orElse(null);

        assertNotNull(foundModule);
        assertEquals(module.getId(), foundModule.getId());
    }
}
