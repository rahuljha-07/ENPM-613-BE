package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class QuizDto {

    private UUID id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private BigDecimal passingScore;

    @NotNull
    private List<QuestionDto> questions;

    public static QuizDto from(Quiz quiz, UserRole role) {
        var dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setPassingScore(quiz.getPassingScore());
        dto.setQuestions(createQuestionDtos(quiz, role));
        return dto;
    }

    private static ArrayList<QuestionDto> createQuestionDtos(Quiz quiz, UserRole role) {
        var questionDtos = new ArrayList<QuestionDto>();
        for (var question : quiz.getQuestions()) {
            var questionDto = new QuestionDto();
            questionDto.setId(question.getId());
            questionDto.setText(question.getText());
            questionDto.setType(question.getType());
            questionDto.setPoints(question.getPoints());
            questionDto.setOptions(createOptionsDtos(role, question));
            questionDtos.add(questionDto);
        }
        return questionDtos;
    }

    private static List<QuestionOptionDto> createOptionsDtos(UserRole role, Question question) {
        var optionDtos = new ArrayList<QuestionOptionDto>();
        for (var option : question.getOptions()) {
            var optionDto = new QuestionOptionDto();
            optionDto.setId(option.getId());
            optionDto.setText(option.getText());
            // Do not expose isCorrect to students
            if (role.equals(UserRole.ADMIN) || role.equals(UserRole.INSTRUCTOR)) {
                optionDto.setIsCorrect(option.isCorrect());
            }
            optionDtos.add(optionDto);
        }
        return optionDtos;
    }
}
