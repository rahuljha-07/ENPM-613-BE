package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.ModuleDto;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final UserService userService;

    @GetMapping("/module/{moduleId}")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<Res<Object>> findModuleByIdAsStudent(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID moduleId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        var module = moduleService.findCourseModuleById(user, moduleId);
        return Reply.ok(module);
    }

    @PostMapping("/instructor/course/{courseId}/add-module")
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

    @PutMapping("/instructor/update-module/{moduleId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> updateModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID moduleId,
        @Valid @RequestBody ModuleDto dto
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        moduleService.updateCourseModule(user, moduleId, dto);
        return Reply.ok("Module updated successfully.");
    }

    @DeleteMapping("/instructor/delete-module/{moduleId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiRes<Res<String>> deleteCourseModule(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID moduleId
    ) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        moduleService.deleteCourseModule(user, moduleId);
        return Reply.ok("Module deleted successfully.");
    }

//    @PutMapping("/instructor/module/{moduleId}/reorder-items")
//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    public ApiRes<Res<String>> reorderModuleItems(
//        @AuthenticationPrincipal Jwt jwt,
//        @PathVariable UUID moduleId,
//        @RequestBody List<UUID> itemsOrder
//    ) {
//        var user = userService.findById(jwt.getClaimAsString("sub"));
//        moduleService.reorderModuleItems(user, moduleId, itemsOrder);
//        return Reply.ok("Course modules reordered successfully.");
//    }
}
