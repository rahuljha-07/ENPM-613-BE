package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.QuizAttempt;
import com.github.ilim.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, UUID> {

    List<QuizAttempt> findQuizAttemptsByQuizAndStudent(Quiz quiz, User student);

    List<QuizAttempt> findQuizAttemptsByQuiz(Quiz quiz);

}
