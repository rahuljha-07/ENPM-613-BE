package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Video;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class VideoRepoTest {

    @Autowired
    private VideoRepo videoRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void testSaveAndFindById() {
        Video video = new Video();
        video.setTitle("Video 1");
        video.setVideoUrl("http://example.com/video.mp4");
        videoRepo.save(video);

        Video foundVideo = videoRepo.findById(video.getId()).orElse(null);

        assertNotNull(foundVideo);
        assertEquals(video.getId(), foundVideo.getId());
    }
}
