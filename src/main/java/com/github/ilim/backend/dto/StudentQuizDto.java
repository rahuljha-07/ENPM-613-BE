package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Quiz;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StudentQuizDto(
    UUID id,
    String title,
    String description,
    BigDecimal passingScore,
    UUID courseModuleId,
    List<StudentQuestionDto> questions
) {
    public static StudentQuizDto from(Quiz quiz) {
        return new StudentQuizDto(
            quiz.getId(),
            quiz.getTitle(),
            quiz.getDescription(),
            quiz.getPassingScore(),
            quiz.getCourseModule().getId(),
            quiz.getQuestions().stream().map(StudentQuestionDto::from).toList()
        );
    }
}
