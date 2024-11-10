package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QuestionOptionDto {

    private UUID id;

    @NotBlank
    private String text;

    @NotNull
    private Boolean isCorrect;
}
