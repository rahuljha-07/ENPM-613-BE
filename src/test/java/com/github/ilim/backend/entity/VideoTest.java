package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.VideoDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoTest {

    @Test
    void testFromDto() {
        VideoDto dto = new VideoDto();
        dto.setTitle("Introduction to Java");
        dto.setDescription("Basic concepts of Java");
        dto.setVideoUrl("http://example.com/video.mp4");
        dto.setTranscriptUrl("http://example.com/transcript.txt");
        dto.setDurationInSeconds(600);

        Video video = Video.from(dto);

        assertEquals(dto.getTitle(), video.getTitle());
        assertEquals(dto.getDescription(), video.getDescription());
        assertEquals(dto.getVideoUrl(), video.getVideoUrl());
        assertEquals(dto.getTranscriptUrl(), video.getTranscriptUrl());
        assertEquals(dto.getDurationInSeconds(), video.getDurationInSeconds());
    }

    @Test
    void testUpdateFromDto() {
        VideoDto dto = new VideoDto();
        dto.setTitle("Advanced Java");
        dto.setDescription("Deep dive into Java");
        dto.setVideoUrl("http://example.com/advanced.mp4");
        dto.setTranscriptUrl("http://example.com/advanced.txt");
        dto.setDurationInSeconds(1200);

        Video video = new Video();
        video.updateFrom(dto);

        assertEquals(dto.getTitle(), video.getTitle());
        assertEquals(dto.getDescription(), video.getDescription());
        assertEquals(dto.getVideoUrl(), video.getVideoUrl());
        assertEquals(dto.getTranscriptUrl(), video.getTranscriptUrl());
        assertEquals(dto.getDurationInSeconds(), video.getDurationInSeconds());
    }
}
