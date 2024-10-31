package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuestionRepo extends JpaRepository<Question, UUID> {

}
