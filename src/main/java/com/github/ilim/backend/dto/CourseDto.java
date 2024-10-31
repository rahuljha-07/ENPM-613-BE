package com.github.ilim.backend.dto;


import com.github.ilim.backend.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseDto {

    @NotNull(message = "instructorTitle cannot be null")
    private String title;

    @NotNull(message = "description cannot be null")
    private String description;

    @NotNull(message = "price cannot be null")
    private BigDecimal price;

    private String thumbnailUrl;
    private String transcriptUrl;
}