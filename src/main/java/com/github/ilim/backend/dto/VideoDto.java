package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Video;
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

    public static VideoDto from(Video video) {
        var dto = new VideoDto();
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getDescription());
        dto.setVideoUrl(video.getVideoUrl());
        dto.setDurationInSeconds(video.getDurationInSeconds());
        dto.setTranscriptUrl(video.getTranscriptUrl());
        return dto;
    }
}
