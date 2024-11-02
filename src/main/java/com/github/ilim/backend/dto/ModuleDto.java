package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModuleDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Integer orderIndex;
}
