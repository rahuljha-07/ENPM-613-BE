package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.UpdateUserDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for managing user profiles.
 * <p>
 * Provides endpoints for retrieving and updating the authenticated user's profile information.
 * </p>
 *
 * @see UserService
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves the profile of the authenticated user.
     * <p>
     * Fetches the {@link User} entity associated with the authenticated user's ID.
     * </p>
     *
     * @param jwt the JWT token representing the authenticated user
     * @return an {@link ApiRes} containing the {@link User} entity with the user's profile details
     */
    @GetMapping
    public ApiRes<Res<User>> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return Reply.ok(userService.findById(userId));
    }

    /**
     * Updates the profile of the authenticated user.
     * <p>
     * Accepts an {@link UpdateUserDto} containing the updated user details,
     * retrieves the authenticated user, and updates their profile accordingly.
     * </p>
     *
     * @param jwt the JWT token representing the authenticated user
     * @param dto the update user data transfer object containing updated profile details
     * @return an {@link ApiRes} containing a success message upon successful update of the user's profile
     */
    @PutMapping
    public ApiRes<Res<String>> updateUserProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UpdateUserDto dto) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        userService.updateFromDto(user, dto);
        return Reply.ok("User data has been updated successfully");
    }

}
