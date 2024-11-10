package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserAnswerDto {

    @NotNull
    private UUID questionId;

    @NotEmpty
    private List<UUID> selectedOptionIds;

}

