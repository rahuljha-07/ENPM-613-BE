package com.github.ilim.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InstructorAppDto {
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private String schoolName;
    private String degreeTitle;
    private LocalDate graduateDate;
    private String professionalTitle;
    private String teachingExperience;
    private int experienceYears;
    private String adminMessage;

    @NotNull(message = "instructorTitle cannot be null")
    private String instructorTitle;

    @NotNull(message = "instructorBio cannot be null")
    private String instructorBio;

    private String videoApplicationUrl;
    private String profileImageUrl;
    private String resumeUrl;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;
}