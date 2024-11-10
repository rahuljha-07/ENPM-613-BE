package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.QuizAttempt;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class QuizAttemptResultDto {

    @NotNull
    private UUID attemptId;

    @NotNull
    private BigDecimal userScore;

    @NotNull
    private BigDecimal totalScore;

    private boolean passed;

    public static QuizAttemptResultDto from(QuizAttempt attempt) {
        var result = new QuizAttemptResultDto();
        result.setAttemptId(attempt.getId());
        result.setUserScore(attempt.getUserScore());
        result.setTotalScore(attempt.getTotalScore());
        result.setPassed(attempt.isPassed());
        return result;
    }
}
