package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupportIssueDto {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    private PriorityLevel priority = PriorityLevel.LOW;  // Default to LOW
}
