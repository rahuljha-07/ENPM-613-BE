package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.VideoDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
@ActiveProfiles("test")
@Import({VideoController.class})
public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User instructor;
    private User student;
    private Video video;
    private UUID videoId;
    private UUID moduleId;
    private VideoDto videoDto;

    @BeforeEach
    void setUp() {
        // Create instructor user
        instructor = new User();
        instructor.setId("instructor123");
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor User");
        instructor.setRole(UserRole.INSTRUCTOR);

        // Create student user
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student User");
        student.setRole(UserRole.STUDENT);

        videoId = UUID.randomUUID();
        moduleId = UUID.randomUUID();

        video = new Video();
        video.setId(videoId);
        video.setTitle("Test Video");
        video.setVideoUrl("http://example.com/video.mp4");
        video.setDurationInSeconds(120);

        videoDto = new VideoDto();
        videoDto.setTitle("New Video");
        videoDto.setVideoUrl("http://example.com/new-video.mp4");
        videoDto.setDurationInSeconds(150);
    }

    @Test
    void testGetCourseModuleVideo() throws Exception {
        when(userService.findById(student.getId())).thenReturn(student);
        when(videoService.findVideoById(student, videoId)).thenReturn(video);

        mockMvc.perform(get("/video/" + videoId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", student.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.title").value("Test Video"))
            .andExpect(jsonPath("$.body.videoUrl").value("http://example.com/video.mp4"));

        verify(videoService, times(1)).findVideoById(student, videoId);
    }

    @Test
    void testAddVideoToModule() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(post("/instructor/module/" + moduleId + "/add-video")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(videoDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isBadRequest());

        videoService.addVideoToModule(instructor, moduleId, videoDto);
    }

    @Test
    void testUpdateVideo() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(put("/instructor/update-video/" + videoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(videoDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isBadRequest());

        videoService.updateVideo(instructor, videoId, videoDto);
    }

    @Test
    void testRemoveVideoFromModule() throws Exception {
        when(userService.findById(instructor.getId())).thenReturn(instructor);

        mockMvc.perform(delete("/instructor/delete-video/" + videoId)
                .with(jwt().jwt(jwt -> jwt.claim("sub", instructor.getId())).authorities(() -> "ROLE_INSTRUCTOR")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("Video removed successfully from the module"));

        verify(videoService, times(1)).removeVideoFromModule(instructor, videoId);
    }
}
