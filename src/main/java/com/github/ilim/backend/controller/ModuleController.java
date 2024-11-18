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

/**
 * REST controller for managing course modules.
 * <p>
 * Provides endpoints for retrieving, adding, updating, and deleting course modules.
 * </p>
 *
 * @see ModuleService
 * @see UserService
 */
@RestController
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final UserService userService;

    /**
     * Retrieves a specific course module by its ID for an authenticated user.
     * <p>
     * Extracts the user's information from the JWT, invokes the {@link ModuleService#findCourseModuleById(User, UUID)} method,
     * and returns the requested module details.
     * </p>
     *
     * @param jwt      the JWT token containing the user's authentication details
     * @param moduleId the ID of the module to retrieve
     * @return an {@link ApiRes} containing the module details as an {@link Object}
     */
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

    /**
     * Adds a new module to a specific course as an instructor.
     * <p>
     * Extracts the instructor's information from the JWT, invokes the {@link ModuleService#addModuleToCourse(User, UUID, ModuleDto)} method,
     * and returns a response indicating successful addition.
     * </p>
     *
     * @param jwt      the JWT token containing the instructor's authentication details
     * @param courseId the ID of the course to add the module to
     * @param dto      the module creation request payload containing module details
     * @return an {@link ApiRes} containing a success message
     */
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

    /**
     * Updates an existing course module as an instructor.
     * <p>
     * Extracts the instructor's information from the JWT, invokes the {@link ModuleService#updateCourseModule(User, UUID, ModuleDto)} method,
     * and returns a response indicating successful update.
     * </p>
     *
     * @param jwt      the JWT token containing the instructor's authentication details
     * @param moduleId the ID of the module to update
     * @param dto      the module update request payload containing updated module details
     * @return an {@link ApiRes} containing a success message
     */
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

    /**
     * Deletes a specific course module as an instructor.
     * <p>
     * Extracts the instructor's information from the JWT, invokes the {@link ModuleService#deleteCourseModule(User, UUID)} method,
     * and returns a response indicating successful deletion.
     * </p>
     *
     * @param jwt      the JWT token containing the instructor's authentication details
     * @param moduleId the ID of the module to delete
     * @return an {@link ApiRes} containing a success message
     */
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

    // /**
    //  * Reorders items within a course module as an instructor.
    //  * <p>
    //  * Extracts the instructor's information from the JWT, invokes the {@link ModuleService#reorderModuleItems(User, UUID, List)} method,
    //  * and returns a response indicating successful reordering.
    //  * </p>
    //  *
    //  * @param jwt        the JWT token containing the instructor's authentication details
    //  * @param moduleId   the ID of the module whose items are to be reordered
    //  * @param itemsOrder the list of item IDs in the desired order
    //  * @return an {@link ApiRes} containing a success message
    //  */
    // @PutMapping("/instructor/module/{moduleId}/reorder-items")
    // @PreAuthorize("hasRole('INSTRUCTOR')")
    // public ApiRes<Res<String>> reorderModuleItems(
    //     @AuthenticationPrincipal Jwt jwt,
    //     @PathVariable UUID moduleId,
    //     @RequestBody List<UUID> itemsOrder
    // ) {
    //     var user = userService.findById(jwt.getClaimAsString("sub"));
    //     moduleService.reorderModuleItems(user, moduleId, itemsOrder);
    //     return Reply.ok("Course modules reordered successfully.");
    // }
}