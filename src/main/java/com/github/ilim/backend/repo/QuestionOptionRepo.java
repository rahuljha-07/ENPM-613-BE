package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuestionOptionRepo extends JpaRepository<QuestionOption, UUID> {

}
