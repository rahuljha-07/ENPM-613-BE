package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RespondToInstructorAppDto {
    @NotNull(message = "instructorApplicationId cannot be null")
    private String instructorApplicationId;
    private String message;
}
