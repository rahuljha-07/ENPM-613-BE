package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoDto {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "videoUrl is required")
    private String videoUrl;

    private int durationInSeconds;

    private String transcriptUrl;

}
