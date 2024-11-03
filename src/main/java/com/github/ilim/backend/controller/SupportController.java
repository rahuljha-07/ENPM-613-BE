package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.SupportIssueDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.SupportService;
import com.github.ilim.backend.service.UserService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/support")
public class SupportController {

    private final SupportService supportService;
    private final UserService userService;

    @PostMapping("/issues")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    public ApiRes<Res<String>> submitSupportIssue(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SupportIssueDto supportIssueDto) {
        String userId = jwt.getClaimAsString("sub");
        User user = userService.findById(userId);
        supportService.sendSupportIssueEmail(user, supportIssueDto);
        return Reply.ok("Your support issue has been submitted successfully.");
    }
}

