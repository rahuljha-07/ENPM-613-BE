package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, UUID> {

}
