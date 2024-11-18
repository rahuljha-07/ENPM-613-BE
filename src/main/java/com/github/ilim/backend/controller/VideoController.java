package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.VideoDto;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.service.VideoService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller responsible for managing video content.
 * <p>
 * Provides endpoints for retrieving videos, adding new videos to modules,
 * updating existing videos, and removing videos from modules.
 * </p>
 *
 * @see VideoService
 * @see UserService
 */
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final UserService userService;

    /**
     * Retrieves a video by its unique identifier.
     * <p>
     * Fetches the video details for the specified video ID, accessible to authenticated users.
     * </p>
     *
     * @param jwt     the JWT token representing the authenticated user
     * @param videoId the unique identifier of the video to retrieve
     * @return an {@link ApiRes} containing the {@link Video} entity representing the video details
     */
    @GetMapping("/video/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Res<Video>> getCourseModuleVideo(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID videoId) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var video = videoService.findVideoById(user, videoId);
        return Reply.ok(video);
    }

    /**
     * Adds a new video to a specific module.
     * <p>
     * Accepts a {@link VideoDto} containing video details, associates it with the specified module ID,
     * and adds the video to the module.
     * </p>
     *
     * @param jwt      the JWT token representing the authenticated instructor
     * @param moduleId the unique identifier of the module to which the video will be added
     * @param dto      the video data transfer object containing video details
     * @return an {@link ApiRes} containing a success message upon successful addition of the video
     */
    @PostMapping("/instructor/module/{moduleId}/add-video")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> addVideoToModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID moduleId,
        @Valid @RequestBody VideoDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.addVideoToModule(user, moduleId, dto);
        return Reply.created("Video added successfully to the module");
    }

    /**
     * Updates an existing video with new details.
     * <p>
     * Accepts a {@link VideoDto} containing updated video details, associates it with the specified video ID,
     * and updates the video accordingly.
     * </p>
     *
     * @param jwt     the JWT token representing the authenticated instructor
     * @param videoId the unique identifier of the video to update
     * @param dto     the video data transfer object containing updated video details
     * @return an {@link ApiRes} containing a success message upon successful update of the video
     */
    @PutMapping("/instructor/update-video/{videoId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> updateVideo(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID videoId,
        @Valid @RequestBody VideoDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.updateVideo(user, videoId, dto);
        return Reply.ok("Video updated successfully");
    }

    /**
     * Removes a video from a specific module.
     * <p>
     * Deletes the association between the specified video ID and its module, effectively removing the video.
     * </p>
     *
     * @param jwt     the JWT token representing the authenticated instructor
     * @param videoId the unique identifier of the video to remove
     * @return an {@link ApiRes} containing a success message upon successful removal of the video
     */
    @DeleteMapping("/instructor/delete-video/{videoId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> removeVideoFromModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID videoId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.removeVideoFromModule(user, videoId);
        return Reply.ok("Video removed successfully from the module");
    }
}
