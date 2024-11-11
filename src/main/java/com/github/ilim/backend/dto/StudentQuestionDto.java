package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.enums.QuestionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StudentQuestionDto(
    UUID id,
    String text,
    QuestionType type,
    BigDecimal points,
    int orderIndex,
    UUID quizId,
    List<StudentQuestionOptionDto> options
) {
    public static StudentQuestionDto from(Question question) {
        return new StudentQuestionDto(
            question.getId(),
            question.getText(),
            question.getType(),
            question.getPoints(),
            question.getOrderIndex(),
            question.getQuizId(),
            question.getOptions().stream().map(StudentQuestionOptionDto::from).toList()
        );
    }
}
