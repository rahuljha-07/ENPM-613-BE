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

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final UserService userService;

    @GetMapping("/course/{courseId}/module/{moduleId}/video/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Res<Video>> getCourseModuleVideo(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId,
        @PathVariable UUID videoId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var video = videoService.getCourseModuleVideo(user, courseId, moduleId, videoId);
        return Reply.ok(video);
    }

    @PostMapping("/instructor/course/{courseId}/module/{moduleId}/video")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> addVideoToModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId,
        @Valid @RequestBody VideoDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.addVideoToModule(user, courseId, moduleId, dto);
        return Reply.created("Video added successfully to the module");
    }

    @PutMapping("/instructor/course/{courseId}/module/{moduleId}/video/{videoId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> updateVideo(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId,
        @PathVariable UUID videoId,
        @Valid @RequestBody VideoDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.updateVideo(user, courseId, moduleId, videoId, dto);
        return Reply.created("Video updated successfully");
    }

    @DeleteMapping("/instructor/course/{courseId}/module/{moduleId}/video/{videoId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> removeVideoFromModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId,
        @PathVariable UUID videoId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.removeVideoFromModule(user, courseId, moduleId, videoId);
        return Reply.created("Video removed successfully from the module");
    }
}
