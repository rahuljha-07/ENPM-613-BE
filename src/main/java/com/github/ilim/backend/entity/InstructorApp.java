package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "instructor_applications")
@Data
@NoArgsConstructor
public class InstructorApp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userId;

    private String profileImageUrl;

    private String schoolName;
    private String degreeTitle;
    private LocalDate graduateDate;

    private String professionalTitle;
    private int experienceYears;
    private String resumeUrl;

    @Column(length = 2000)
    private String teachingExperience;

    private String instructorTitle;

    @Column(length = 2000)
    private String instructorBio;

    private String videoApplicationUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private String adminMessage; // in case of rejection

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;

    public static InstructorApp from(InstructorAppDto dto) {
        var application = new InstructorApp();
        application.profileImageUrl = dto.getProfileImageUrl();
        application.schoolName = dto.getSchoolName();
        application.degreeTitle = dto.getDegreeTitle();
        application.graduateDate = dto.getGraduateDate();
        application.professionalTitle = dto.getProfessionalTitle();
        application.experienceYears = dto.getExperienceYears();
        application.resumeUrl = dto.getResumeUrl();
        application.teachingExperience = dto.getTeachingExperience();
        application.instructorTitle = dto.getInstructorTitle();
        application.instructorBio = dto.getInstructorBio();
        application.videoApplicationUrl = dto.getVideoApplicationUrl();
        application.status = dto.getStatus();
        application.adminMessage = dto.getAdminMessage();
        application.submittedAt = dto.getSubmittedAt();
        application.reviewedAt = dto.getReviewedAt();
        return application;
    }
}
