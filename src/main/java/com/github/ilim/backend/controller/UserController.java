package com.github.ilim.backend.controller;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping
    public User getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return userService.findById(userId);
    }

}
