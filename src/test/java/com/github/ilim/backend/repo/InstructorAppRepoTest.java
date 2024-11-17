package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InstructorAppRepoTest {

    @Autowired
    private InstructorAppRepo instructorAppRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private InstructorApp app1;
    private InstructorApp app2;

    @BeforeEach
    void setUp() {
        app1 = new InstructorApp();
        app1.setUserId("user-1");
        app1.setSchoolName("University A");
        app1.setStatus(ApplicationStatus.PENDING);
        app1.setGraduateDate(LocalDate.now());
        instructorAppRepo.save(app1);

        app2 = new InstructorApp();
        app2.setUserId("user-2");
        app2.setSchoolName("University B");
        app2.setStatus(ApplicationStatus.APPROVED);
        app2.setGraduateDate(LocalDate.now());
        instructorAppRepo.save(app2);
    }

    @Test
    void testFindByStatus() {
        List<InstructorApp> pendingApps = instructorAppRepo.findByStatus(ApplicationStatus.PENDING, Sort.unsorted());

        assertEquals(1, pendingApps.size());
        assertEquals(app1.getId(), pendingApps.getFirst().getId());
    }

    @Test
    void testFindByUserId() {
        List<InstructorApp> userApps = instructorAppRepo.findByUserId("user-1", Sort.unsorted());

        assertEquals(1, userApps.size());
        assertEquals(app1.getId(), userApps.getFirst().getId());
    }
}
