package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.RespondToInstructorAppDto;
import com.github.ilim.backend.service.AdminService;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("approve-instructor-application")
    public ApiRes<Res<String>> approveInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.approveInstructorApp(dto.getInstructorApplicationId());
        return Reply.ok("Application approved");
    }

    @PostMapping("reject-instructor-application")
    public ApiRes<Res<String>> rejectInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.rejectInstructorApp(dto.getInstructorApplicationId(), dto.getMessage());
        return Reply.ok("Application rejected");
    }
}
