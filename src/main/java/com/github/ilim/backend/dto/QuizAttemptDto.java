package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class QuizAttemptDto {

    private UUID id; // For retrieval

    @NotNull
    private UUID quizId;

    private Integer score;

    private Boolean passed;

    @NotNull
    private List<UserAnswerDto> answers;
}
