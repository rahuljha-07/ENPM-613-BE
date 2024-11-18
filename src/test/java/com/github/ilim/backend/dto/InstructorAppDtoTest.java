package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InstructorAppDtoTest {

    @Test
    void testInstructorAppDtoFields() {
        InstructorAppDto dto = new InstructorAppDto();
        dto.setStatus(ApplicationStatus.APPROVED);
        dto.setSchoolName("University of Example");
        dto.setDegreeTitle("Bachelor of Science");
        dto.setGraduateDate(LocalDate.of(2020, 5, 15));
        dto.setProfessionalTitle("Senior Developer");
        dto.setTeachingExperience("5 years of teaching experience.");
        dto.setExperienceYears(5);
        dto.setAdminMessage("Application approved.");
        dto.setInstructorTitle("Dr.");
        dto.setInstructorBio("Experienced instructor in computer science.");
        dto.setVideoApplicationUrl("http://example.com/video");
        dto.setProfileImageUrl("http://example.com/profile.jpg");
        dto.setResumeUrl("http://example.com/resume.pdf");
        dto.setReviewedAt(LocalDateTime.now());

        assertEquals(ApplicationStatus.APPROVED, dto.getStatus());
        assertEquals("University of Example", dto.getSchoolName());
        assertEquals("Bachelor of Science", dto.getDegreeTitle());
        assertEquals(LocalDate.of(2020, 5, 15), dto.getGraduateDate());
        assertEquals("Senior Developer", dto.getProfessionalTitle());
        assertEquals("5 years of teaching experience.", dto.getTeachingExperience());
        assertEquals(5, dto.getExperienceYears());
        assertEquals("Application approved.", dto.getAdminMessage());
        assertEquals("Dr.", dto.getInstructorTitle());
        assertEquals("Experienced instructor in computer science.", dto.getInstructorBio());
        assertEquals("http://example.com/video", dto.getVideoApplicationUrl());
        assertEquals("http://example.com/profile.jpg", dto.getProfileImageUrl());
        assertEquals("http://example.com/resume.pdf", dto.getResumeUrl());
        assertNotNull(dto.getReviewedAt());
    }
}
