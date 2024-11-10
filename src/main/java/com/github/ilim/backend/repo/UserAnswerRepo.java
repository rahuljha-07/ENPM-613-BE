package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAnswerRepo extends JpaRepository<UserAnswer, UUID> {

}
