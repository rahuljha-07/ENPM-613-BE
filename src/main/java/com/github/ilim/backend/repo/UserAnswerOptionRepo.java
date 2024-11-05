package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.UserAnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAnswerOptionRepo extends JpaRepository<UserAnswerOption, UUID> {

}
