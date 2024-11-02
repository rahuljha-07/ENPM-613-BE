package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InstructorAppService InstructorAppService;
    private final UserService userService;

    public void approveInstructorApp(String applicationId) {
        var application = InstructorAppService.findById(applicationId);
        if (application.getStatus() == ApplicationStatus.APPROVED) {
            throw new BadRequestException("Application already approved");
        }
        updateInstructorAppStatus(application, ApplicationStatus.APPROVED, null);
        userService.promoteToInstructor(application);
        // TODO: notify user
    }

    public void rejectInstructorApp(String applicationId, String reason) {
        var application = InstructorAppService.findById(applicationId);
        if (application.getStatus() == ApplicationStatus.REJECTED) {
            throw new BadRequestException("Application already rejected");
        }
        updateInstructorAppStatus(application, ApplicationStatus.REJECTED, reason);
        // TODO: notify user
    }

    private void updateInstructorAppStatus(InstructorApp application, ApplicationStatus status, String reason) {
        application.setStatus(status);
        application.setAdminMessage(reason);
        application.setReviewedAt(LocalDateTime.now());
        InstructorAppService.update(application);
    }

}
