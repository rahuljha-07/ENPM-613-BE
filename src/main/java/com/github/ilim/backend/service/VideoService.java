package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.VideoDto;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.exception.exceptions.VideoNotFoundException;
import com.github.ilim.backend.repo.VideoRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class responsible for managing videos within course modules.
 * <p>
 * Provides functionalities such as adding, removing, and updating videos,
 * as well as retrieving videos based on their unique identifiers.
 * </p>
 *
 * @see VideoRepo
 * @see CourseService
 * @see ModuleService
 * @see ModuleItemService
 */
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepo videoRepo;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final ModuleItemService moduleItemService;

    /**
     * Adds a new video to a specified module.
     * <p>
     * Creates a new {@link Video} entity from the provided {@link VideoDto}, associates it with the module,
     * saves the video, and updates the module's items.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor adding the video
     * @param moduleId   the unique identifier of the module to which the video will be added
     * @param dto        the data transfer object containing video details
     */
    @Transactional
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

    /**
     * Removes a video from a specified module.
     * <p>
     * Finds the video by its ID, ensures the instructor is authorized to remove it,
     * removes the video from the module's items, and deletes the video record.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor removing the video
     * @param videoId    the unique identifier of the video to be removed
     * @throws NotCourseInstructorException if the user is not the instructor of the course
     * @throws VideoNotFoundException       if the video is not found
     */
    @Transactional
    public void removeVideoFromModule(User instructor, UUID videoId) {
        var video = findVideoByIdAsInstructor(instructor, videoId);
        var module = video.getCourseModule();
        var moduleItem = moduleItemService.findModuleItemByVideo(video);
        module.removeModuleItem(moduleItem);
        videoRepo.delete(video);
    }

    /**
     * Updates an existing video with new details.
     * <p>
     * Finds the video by its ID, ensures the instructor is authorized to update it,
     * updates the video's details based on the provided {@link VideoDto}, and saves the updated video.
     * </p>
     *
     * @param instructor the {@link User} entity representing the instructor updating the video
     * @param videoId    the unique identifier of the video to be updated
     * @param dto        the data transfer object containing updated video details
     * @throws NotCourseInstructorException if the user is not the instructor of the course
     * @throws VideoNotFoundException       if the video is not found
     */
    @Transactional
    public void updateVideo(User instructor, UUID videoId, @Valid VideoDto dto) {
        var video = findVideoByIdAsInstructor(instructor, videoId);
        video.updateFrom(dto);
        videoRepo.save(video);
    }

    /**
     * Finds a video by its unique identifier as an instructor, ensuring authorization.
     *
     * @param instructor the {@link User} entity representing the instructor
     * @param videoId    the unique identifier of the video to find
     * @return the {@link Video} entity corresponding to the provided ID
     * @throws NotCourseInstructorException if the user is not the instructor of the course
     * @throws VideoNotFoundException       if the video is not found
     */
    public Video findVideoByIdAsInstructor(User instructor, UUID videoId) {
        var video = findVideoById(instructor, videoId);
        if (!video.getCourseModule().getCourse().getInstructor().equals(instructor)) {
            throw new NotCourseInstructorException(instructor, video);
        }
        return video;
    }

    /**
     * Finds a video by its unique identifier, ensuring the user has access to its content.
     *
     * @param instructor the {@link User} entity requesting the video
     * @param videoId    the unique identifier of the video
     * @return the {@link Video} entity corresponding to the provided ID
     * @throws VideoNotFoundException    if the video is not found
     * @throws NotCourseInstructorException if the user does not have access to the video content
     */
    public Video findVideoById(User instructor, UUID videoId) {
        var video = videoRepo.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException(videoId));
        var course = video.getCourseModule().getCourse();
        courseService.assertUserHasAccessToCourseContent(instructor, course);
        return video;
    }
}
