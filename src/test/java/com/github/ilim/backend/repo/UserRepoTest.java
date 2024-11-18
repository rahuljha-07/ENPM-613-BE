package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId("user-1");
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        user1.setBirthdate(LocalDate.now());
        user1.setRole(UserRole.STUDENT);
        user1 = userRepo.save(user1);

        user2 = new User();
        user2.setId("user-2");
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        user2.setBirthdate(LocalDate.now());
        user2.setRole(UserRole.INSTRUCTOR);
        user2 = userRepo.save(user2);
    }

    @Test
    void testFindByEmail() {
        Optional<User> foundUser = userRepo.findByEmail("user1@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user1.getId(), foundUser.get().getId());
    }

    @Test
    void testFindByRole() {
        List<User> instructors = userRepo.findByRole(UserRole.INSTRUCTOR, Sort.unsorted());

        assertEquals(1, instructors.size());
        assertEquals(user2.getId(), instructors.getFirst().getId());
    }
}
