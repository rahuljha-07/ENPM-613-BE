package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.VideoDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.enums.CourseStatus;
import com.github.ilim.backend.enums.ModuleItemType;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.exception.exceptions.VideoNotFoundException;
import com.github.ilim.backend.repo.CourseRepo;
import com.github.ilim.backend.repo.ModuleRepo;
import com.github.ilim.backend.repo.UserRepo;
import com.github.ilim.backend.repo.VideoRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@DataJpaTest
public class VideoServiceTest {

    private VideoService videoService;

    @Autowired
    private VideoRepo videoRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModuleRepo moduleRepo;

    // Mock dependencies
    @Mock
    private CourseService courseService;

    @Mock
    private ModuleService moduleService;

    @Mock
    private ModuleItemService moduleItemService;
    @Autowired
    private CourseRepo courseRepo;


    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User instructor;
    private User student;
    private Course course;
    private CourseModule module;
    private Video video;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        videoService = new VideoService(videoRepo, courseService, moduleService, moduleItemService);

        // Create an instructor
        instructor = new User();
        instructor.setId(UUID.randomUUID().toString());
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setBirthdate(LocalDate.now());
        instructor = userRepo.save(instructor);


        // Create a course
        course = new Course();
        course.setTitle("Test Course");
        course.setPrice(new BigDecimal("49.99"));
        course.setStatus(CourseStatus.DRAFT);
        course.setInstructor(instructor);
        course = courseRepo.save(course);

        // Create a module
        module = new CourseModule();
        module.setId(UUID.randomUUID());
        module.setTitle("Module 1");
        module.setCourse(course);
        module = moduleRepo.save(module);
    }

    @Test
    @Transactional
    void testAddVideoToModule_Success() {
        VideoDto videoDto = new VideoDto();
        videoDto.setTitle("New Video");
        videoDto.setVideoUrl("http://example.com/video.mp4");
        videoDto.setDurationInSeconds(120);

        when(moduleService.findModuleByIdAsInstructor(instructor, module.getId())).thenReturn(module);

        videoService.addVideoToModule(instructor, module.getId(), videoDto);

        List<Video> videos = videoRepo.findAll();
        assertEquals(1, videos.size());
        Video addedVideo = videos.getFirst();
        assertEquals("New Video", addedVideo.getTitle());
        assertEquals(module.getId(), addedVideo.getCourseModule().getId());
    }

    @Test
    void testFindVideoByIdAsInstructor_NotInstructor() {
        UUID videoId = UUID.randomUUID();

        Video video = new Video();
        video.setId(videoId);
        video.setCourseModule(module);
        videoRepo.save(video);

        videoRepo.findById(videoId);
        doThrow(new NotCourseInstructorException(student, video)).when(courseService).assertUserHasAccessToCourseContent(any(), any());

        assertThrows(VideoNotFoundException.class, () -> {
            videoService.findVideoByIdAsInstructor(student, videoId);
        });
    }

    @Test
    void testFindVideoById_Success() {
        video = new Video();
        video.setTitle("Test Video");
        video.setCourseModule(module);
        video = videoRepo.save(video);


        when(courseService.findCourseByIdAndUser(student, module.getCourse().getId())).thenReturn(course);

        Video foundVideo = videoService.findVideoById(student, video.getId());

        assertNotNull(foundVideo);
        assertEquals(video.getId(), foundVideo.getId());

    }

    @Test
    void testFindVideoById_VideoNotFound() {
        UUID nonExistentVideoId = UUID.randomUUID();

        assertThrows(VideoNotFoundException.class, () -> {
            videoService.findVideoById(student, nonExistentVideoId);
        });
    }

    @Test
    void testFindVideoById_UserHasNoAccess() {
        Course otherCourse = new Course();
        otherCourse.setTitle("Other Course");
        otherCourse.setInstructor(instructor);
        otherCourse = courseRepo.save(otherCourse);

        CourseModule otherModule = new CourseModule();
        otherModule.setTitle("Other Module");
        otherModule.setCourse(otherCourse);
        otherModule = moduleRepo.save(otherModule);

        Video otherVideo = new Video();
        otherVideo.setId(UUID.randomUUID());
        otherVideo.setTitle("Other Video");
        otherVideo.setCourseModule(otherModule);
        videoRepo.save(otherVideo);

        when(courseService.findCourseByIdAndUser(student, otherCourse.getId()))
            .thenThrow(new UserHasNoAccessToCourseException(student, otherCourse.getId()));

        assertThrows(VideoNotFoundException.class, () -> {
            videoService.findVideoById(student, otherVideo.getId());
        });
    }
    @Test
    @Transactional
    void testUpdateVideo_Success() {
        video = new Video();
        video.setId(UUID.randomUUID());
        video.setTitle("Original Title");
        video.setCourseModule(module);
        video = videoRepo.save(video);

        videoService.findVideoByIdAsInstructor(instructor, video.getId());

        VideoDto videoDto = new VideoDto();
        videoDto.setTitle("Updated Title");
        videoDto.setVideoUrl("http://example.com/updated_video.mp4");
        videoDto.setDurationInSeconds(150);

        videoService.updateVideo(instructor, video.getId(), videoDto);

        Video updatedVideo = videoRepo.findById(video.getId()).orElse(null);
        assertNotNull(updatedVideo);
        assertEquals("Updated Title", updatedVideo.getTitle());
        assertEquals("http://example.com/updated_video.mp4", updatedVideo.getVideoUrl());
        assertEquals(150, updatedVideo.getDurationInSeconds());
    }

    @Test
    void testUpdateVideo_NotInstructor() {
        video = new Video();
        video.setId(UUID.randomUUID());
        video.setTitle("Original Title");
        video.setCourseModule(module);
        video = videoRepo.save(video);

        when(videoService.findVideoByIdAsInstructor(instructor, video.getId()))
            .thenThrow(new NotCourseInstructorException(student, video));

        VideoDto videoDto = new VideoDto();
        videoDto.setTitle("Updated Title");

        assertThrows(NotCourseInstructorException.class, () -> {
            videoService.updateVideo(student, video.getId(), videoDto);
        });
    }

    @Test
    void testUpdateVideo_VideoNotFound() {
        UUID nonExistentVideoId = UUID.randomUUID();

        VideoDto videoDto = new VideoDto();
        videoDto.setTitle("Updated Title");

        assertThrows(VideoNotFoundException.class, () -> {
            videoService.updateVideo(instructor, nonExistentVideoId, videoDto);
        });
    }
    @Test
    @Transactional
    void testRemoveVideoFromModule_Success() {
        video = new Video();
        video.setId(UUID.randomUUID());
        video.setTitle("Test Video");
        video.setCourseModule(module);
        video = videoRepo.save(video);

        videoService.findVideoByIdAsInstructor(instructor, video.getId());

        var item = new CourseModuleItem();
        item.setCourseModule(module);
        item.setVideo(video);
        item.setItemType(ModuleItemType.VIDEO);
        item.setId(UUID.randomUUID());
        when(moduleItemService.findModuleItemByVideo(video)).thenReturn(item);

        videoService.removeVideoFromModule(instructor, video.getId());

        assertFalse(videoRepo.findById(video.getId()).isPresent());
    }

    @Test
    void testRemoveVideoFromModule_NotInstructor() {
        video = new Video();
        video.setId(UUID.randomUUID());
        video.setTitle("Test Video");
        video.setCourseModule(module);
        video = videoRepo.save(video);

        when(videoService.findVideoByIdAsInstructor(instructor, video.getId()))
            .thenThrow(new NotCourseInstructorException(student, video));

        assertThrows(NotCourseInstructorException.class, () -> {
            videoService.removeVideoFromModule(student, video.getId());
        });
    }

    @Test
    void testRemoveVideoFromModule_VideoNotFound() {
        UUID nonExistentVideoId = UUID.randomUUID();

        assertThrows(VideoNotFoundException.class, () -> {
            videoService.removeVideoFromModule(instructor, nonExistentVideoId);
        });
    }

}
