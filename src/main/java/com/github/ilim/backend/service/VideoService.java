package com.github.ilim.backend.service;

import com.fasterxml.jackson.databind.Module;
import com.github.ilim.backend.dto.VideoDto;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.enums.ModuleItemType;
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

    public void addVideoToModule(User instructor, UUID courseId, UUID moduleId, @Valid VideoDto dto) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = course.findModule(moduleId);

        // create video record
        var video = Video.from(dto);
        videoRepo.save(video);

        // add it to module
        var moduleItem = CourseModuleItem.create(video);
        module.addModuleItem(moduleItem);
        moduleService.saveModule(module);
    }

    public void removeVideoFromModule(User instructor, UUID courseId, UUID moduleId, UUID itemId) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = course.findModule(moduleId);
        var moduleItem = module.findModuleItem(itemId);
        var video = moduleItem.getVideo();
        module.removeModuleItem(moduleItem);
        videoRepo.delete(video);
    }

    public Video getCourseModuleVideo(User user, UUID courseId, UUID moduleId, UUID videoId) {
        var course = courseService.findCourseByIdAndUser(user, courseId);
        var module = course.findModule(moduleId);
        var moduleItem = module.findModuleItemByVideoId(videoId);
        return moduleItem.getVideo();
    }

    public void updateVideo(User instructor, UUID courseId, UUID moduleId, UUID videoId, @Valid VideoDto dto) {
        var course = courseService.findCourseByIdAndUser(instructor, courseId);
        var module = course.findModule(moduleId);
        var moduleItem = module.findModuleItemByVideoId(videoId);
        var video = moduleItem.getVideo();
        video.updateFrom(dto);
        videoRepo.save(video);
    }
}
