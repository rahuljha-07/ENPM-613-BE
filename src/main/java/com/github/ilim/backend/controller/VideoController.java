package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.VideoDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final UserService userService;

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

    @DeleteMapping("/instructor/course/{courseId}/module/{moduleId}/video/{itemId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> removeVideoFromModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId,
        @PathVariable UUID itemId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        videoService.removeVideoFromModule(user, courseId, moduleId, itemId);
        return Reply.created("Video removed successfully from the module");
    }
}
