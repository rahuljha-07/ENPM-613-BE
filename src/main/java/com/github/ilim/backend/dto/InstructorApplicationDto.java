package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InstructorApplicationDto {
    private String profileImageUrl;
    private String schoolName;
    private String degreeTitle;
    private LocalDate graduateDate;
    private String professionalTitle;
    private int experienceYears;
    private String resumeUrl;
    private String teachingExperience;
    private String instructorTitle;
    private String instructorBio;
    private String videoApplicationUrl;
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private String adminMessage;
    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;
}