package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionOptionRepo extends JpaRepository<QuestionOption, UUID> {

    List<QuestionOption> findByQuestionAndIsCorrect(Question question, boolean isCorrect);

}
