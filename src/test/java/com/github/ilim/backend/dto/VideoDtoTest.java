package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Video;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VideoDtoTest {

    @Test
    void testFrom() {
        // Create Video
        Video video = new Video();
        video.setTitle("Java Streams Tutorial");
        video.setDescription("Comprehensive guide to Java Streams.");
        video.setVideoUrl("http://example.com/video.mp4");
        video.setDurationInSeconds(600);
        video.setTranscriptUrl("http://example.com/transcript.pdf");

        VideoDto dto = VideoDto.from(video);

        assertEquals("Java Streams Tutorial", dto.getTitle());
        assertEquals("Comprehensive guide to Java Streams.", dto.getDescription());
        assertEquals("http://example.com/video.mp4", dto.getVideoUrl());
        assertEquals(600, dto.getDurationInSeconds());
        assertEquals("http://example.com/transcript.pdf", dto.getTranscriptUrl());
    }

    @Test
    void testVideoDtoFields() {
        VideoDto dto = new VideoDto();
        dto.setTitle("Java Streams Tutorial");
        dto.setDescription("Comprehensive guide to Java Streams.");
        dto.setVideoUrl("http://example.com/video.mp4");
        dto.setDurationInSeconds(600);
        dto.setTranscriptUrl("http://example.com/transcript.pdf");

        assertEquals("Java Streams Tutorial", dto.getTitle());
        assertEquals("Comprehensive guide to Java Streams.", dto.getDescription());
        assertEquals("http://example.com/video.mp4", dto.getVideoUrl());
        assertEquals(600, dto.getDurationInSeconds());
        assertEquals("http://example.com/transcript.pdf", dto.getTranscriptUrl());
    }
}
