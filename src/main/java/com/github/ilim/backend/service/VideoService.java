package com.github.ilim.backend.service;

import com.fasterxml.jackson.databind.Module;
import com.github.ilim.backend.dto.VideoDto;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.enums.ModuleItemType;
import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.VideoNotFoundException;
import com.github.ilim.backend.repo.VideoRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepo videoRepo;
    private final CourseService courseService;
    private final ModuleService moduleService;

    public void addVideoToModule(User instructor, UUID moduleId, @Valid VideoDto dto) {
        var module = moduleService.findModuleByIdAsInstructor(instructor, moduleId);

        // create video record
        var video = Video.from(dto);
        video.setCourseModule(module);
        videoRepo.save(video);

        // add it to module
        var moduleItem = CourseModuleItem.create(video);
        module.addModuleItem(moduleItem);
        moduleService.saveModule(module);
    }

    public void removeVideoFromModule(User instructor, UUID itemId) {
        var video = findVideoByIdAsInstructor(instructor, itemId);
        var module = video.getCourseModule();
        var moduleItem = module.findModuleItemByVideoId(itemId);
        module.removeModuleItem(moduleItem);
        videoRepo.delete(video);
    }

    public void updateVideo(User instructor, UUID videoId, @Valid VideoDto dto) {
        var video = findVideoByIdAsInstructor(instructor, videoId);
        video.updateFrom(dto);
        videoRepo.save(video);
    }

    public Video findVideoByIdAsInstructor(User instructor, UUID videoId) {
        var video = findVideoById(instructor, videoId);
        if (!video.getCourseModule().getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, video);
        }
        return video;
    }

    public Video findVideoById(User instructor, UUID videoId) {
        var video = videoRepo.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException(videoId));
        var course = video.getCourseModule().getCourse();
        courseService.assertUserHasAccessToCourseContent(instructor, course);
        return video;
    }
}
