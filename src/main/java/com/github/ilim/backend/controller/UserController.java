package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.UpdateUserDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ApiRes<Res<List<User>>> getAllUsers() {
        return Reply.ok(userService.getAll());
    }

    @GetMapping
    public ApiRes<Res<User>> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return Reply.ok(userService.findById(userId));
    }

    @PutMapping
    public ApiRes<Res<String>> updateUserProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UpdateUserDto dto) {
        var user = userService.findById(jwt.getClaimAsString("sub"));
        userService.update(user, dto);
        return Reply.ok("User data has been updated successfully");
    }

}
