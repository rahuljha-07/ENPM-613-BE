package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.dto.CourseDto;
import com.github.ilim.backend.enums.CourseStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "courses")
@NoArgsConstructor
public class Course extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String thumbnailUrl;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();

    private String transcriptUrl;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonIgnore
    private User instructor;

    @JsonProperty("instructorId")
    public String getInstructorId() {
        return instructor.getId(); // shouldn't check for null
    }


    public void setStatus(CourseStatus status) {
        this.status = status != null ? status : CourseStatus.DRAFT;
    }

    public static Course from(CourseDto dto) {
        var course = new Course();
        course.setTitle(dto.getTitle());
        course.setThumbnailUrl(dto.getThumbnailUrl());
        course.setDescription(dto.getDescription());
        course.setTranscriptUrl(dto.getTranscriptUrl());
        course.setPrice(dto.getPrice());
        return course;
    }
}
