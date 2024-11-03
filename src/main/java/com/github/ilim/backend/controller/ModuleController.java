package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.ModuleDto;
import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.service.ModuleService;
import com.github.ilim.backend.service.UserService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final UserService userService;

    @GetMapping("/course/{courseId}/module/{moduleId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ApiRes<Res<CourseModule>> getCourseModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var module = moduleService.getCourseModule(user, courseId, moduleId);
        return Reply.ok(module);
    }

    @PostMapping("/instructor/course/{courseId}/module")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> addModuleToCourse(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @Valid @RequestBody ModuleDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        moduleService.addModuleToCourse(user, courseId, dto);
        return Reply.created("Module added successfully to course.");
    }

    @PutMapping("/instructor/course/{courseId}/module/{moduleId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> updateCourseModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId,
        @Valid @RequestBody ModuleDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        moduleService.updateCourseModule(user, courseId, moduleId, dto);
        return Reply.ok("Module updated successfully.");
    }

    @DeleteMapping("/instructor/course/{courseId}/module/{moduleId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> deleteCourseModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @PathVariable UUID moduleId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        moduleService.deleteCourseModule(user, courseId, moduleId);
        return Reply.ok("Module deleted successfully.");
    }

    @PutMapping("/instructor/course/{courseId}/module/item/reorder")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> reorderCourseModules(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID courseId,
        @RequestBody List<UUID> itemsOrder
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        moduleService.reorderModuleItems(user, courseId, itemsOrder);
        return Reply.ok("Course modules reordered successfully.");
    }
}
