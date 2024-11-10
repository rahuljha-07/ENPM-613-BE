package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class QuestionDto {

    private UUID id;

    @NotBlank
    private String text;

    @NotNull
    private QuestionType type;

    @NotNull
    private BigDecimal points;

    @NotNull
    private List<QuestionOptionDto> options;
}
