package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InstructorAppService instructorAppService;
    private final UserService userService;

    @Transactional
    public void approveInstructorApp(UUID applicationId) {
        var application = instructorAppService.findById(applicationId);
        if (application.getStatus() == ApplicationStatus.APPROVED) {
            throw new BadRequestException("Application already approved");
        }
        updateInstructorAppStatus(application, ApplicationStatus.APPROVED, null);
        userService.promoteToInstructor(application);
        // TODO: notify user
    }

    @Transactional
    public void rejectInstructorApp(UUID applicationId, String reason) {
        var application = instructorAppService.findById(applicationId);
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
        instructorAppService.update(application);
    }

}
