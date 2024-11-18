package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class QuizAttemptRepoTest {

    @Autowired
    private QuizAttemptRepo quizAttemptRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private QuizRepo quizRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId("student-1");
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setBirthdate(LocalDate.now());
        student = userRepo.save(student);

        quiz = new Quiz();
        quiz.setTitle("Quiz 1");
        quiz.setPassingScore(BigDecimal.TEN);
        quizRepo.save(quiz);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        quizAttemptRepo.save(attempt);
    }

    @Test
    void testFindQuizAttemptsByQuizAndStudent() {
        List<QuizAttempt> attempts = quizAttemptRepo.findQuizAttemptsByQuizAndStudent(quiz, student, Sort.unsorted());

        assertEquals(1, attempts.size());
        assertEquals(student.getId(), attempts.getFirst().getStudent().getId());
    }

    @Test
    void testFindQuizAttemptsByQuiz() {
        List<QuizAttempt> attempts = quizAttemptRepo.findQuizAttemptsByQuiz(quiz, Sort.unsorted());

        assertEquals(1, attempts.size());
    }
}
