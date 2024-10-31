package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuizRepo extends JpaRepository<Quiz, UUID> {

}
