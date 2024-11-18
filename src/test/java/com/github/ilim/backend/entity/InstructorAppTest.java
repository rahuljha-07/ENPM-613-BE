package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructorAppTest {

    @Test
    void testFromDto() {
        InstructorAppDto dto = new InstructorAppDto();
        dto.setProfileImageUrl("http://example.com/profile.jpg");
        dto.setSchoolName("Example University");
        dto.setDegreeTitle("Computer Science");
        dto.setGraduateDate(LocalDate.now());
        dto.setProfessionalTitle("Software Engineer");
        dto.setExperienceYears(5);
        dto.setResumeUrl("http://example.com/resume.pdf");
        dto.setTeachingExperience("5 years teaching experience");
        dto.setInstructorTitle("Senior Instructor");
        dto.setInstructorBio("Expert in Java");
        dto.setVideoApplicationUrl("http://example.com/video.mp4");
        dto.setStatus(ApplicationStatus.PENDING);
        dto.setAdminMessage(null);
        dto.setReviewedAt(LocalDateTime.now());

        InstructorApp app = InstructorApp.from(dto);

        assertEquals(dto.getProfileImageUrl(), app.getProfileImageUrl());
        assertEquals(dto.getSchoolName(), app.getSchoolName());
        assertEquals(dto.getDegreeTitle(), app.getDegreeTitle());
        assertEquals(dto.getGraduateDate(), app.getGraduateDate());
        assertEquals(dto.getProfessionalTitle(), app.getProfessionalTitle());
        assertEquals(dto.getExperienceYears(), app.getExperienceYears());
        assertEquals(dto.getResumeUrl(), app.getResumeUrl());
        assertEquals(dto.getTeachingExperience(), app.getTeachingExperience());
        assertEquals(dto.getInstructorTitle(), app.getInstructorTitle());
        assertEquals(dto.getInstructorBio(), app.getInstructorBio());
        assertEquals(dto.getVideoApplicationUrl(), app.getVideoApplicationUrl());
        assertEquals(dto.getStatus(), app.getStatus());
        assertEquals(dto.getAdminMessage(), app.getAdminMessage());
        assertEquals(dto.getReviewedAt(), app.getReviewedAt());
    }
}
