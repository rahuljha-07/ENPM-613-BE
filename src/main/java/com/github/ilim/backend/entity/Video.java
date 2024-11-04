package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ilim.backend.dto.VideoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "videos")
@NoArgsConstructor
@JsonIgnoreProperties({"courseModule"})
public class Video extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private CourseModule courseModule;

    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    private String transcriptUrl;

    @Column(nullable = false)
    private int durationInSeconds;

    public static Video from(@Valid VideoDto dto) {
        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setVideoUrl(dto.getVideoUrl());
        video.setDurationInSeconds(dto.getDurationInSeconds());
        video.setTranscriptUrl(dto.getTranscriptUrl());
        return video;
    }

    public void updateFrom(@Valid VideoDto dto) {
        title = dto.getTitle();
        description = dto.getDescription();
        videoUrl = dto.getVideoUrl();
        durationInSeconds = dto.getDurationInSeconds();
        transcriptUrl = dto.getTranscriptUrl();
    }
}