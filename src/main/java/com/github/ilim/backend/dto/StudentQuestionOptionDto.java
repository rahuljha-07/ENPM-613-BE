package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.QuestionOption;

import java.util.UUID;

public record StudentQuestionOptionDto(
    UUID id,
    UUID questionId,
    String text,
    int orderIndex
) {
    public static StudentQuestionOptionDto from(QuestionOption questionOption) {
        return new StudentQuestionOptionDto(
            questionOption.getId(),
            questionOption.getQuestion().getId(),
            questionOption.getText(),
            questionOption.getOrderIndex()
        );
    }
}
