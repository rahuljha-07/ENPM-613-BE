package com.github.ilim.backend.controller;

import com.github.ilim.backend.dto.RespondToInstructorAppDto;
import com.github.ilim.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> approveInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.approveInstructorApp(dto.getInstructorApplicationId());
        return ResponseEntity.ok("Application approved");
    }

    @PostMapping("reject-instructor-application")
    public ResponseEntity<String> rejectInstructorApplication(@Valid @RequestBody RespondToInstructorAppDto dto) {
        adminService.rejectInstructorApp(dto.getInstructorApplicationId(), dto.getMessage());
        return ResponseEntity.ok("Application rejected");
    }
}
