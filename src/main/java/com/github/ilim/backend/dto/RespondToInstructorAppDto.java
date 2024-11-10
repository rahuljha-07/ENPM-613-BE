package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RespondToInstructorAppDto {
    @NotNull(message = "instructorApplicationId cannot be null")
    private UUID instructorApplicationId;
    private String message;
}
