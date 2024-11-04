package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Integer passingScore;

}
