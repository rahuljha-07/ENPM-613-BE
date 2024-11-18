package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCantBeBlockedException;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.UserIsNotAdminException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class responsible for administrative operations.
 * <p>
 * Provides functionalities such as approving or rejecting instructor applications
 * and blocking users. Interacts with {@link InstructorAppService} and {@link UserService}
 * to perform these operations.
 * </p>
 *
 * @see InstructorAppService
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final InstructorAppService instructorAppService;
    private final UserService userService;

    /**
     * Approves an instructor application.
     * <p>
     * Changes the status of the specified instructor application to {@link ApplicationStatus#APPROVED},
     * promotes the applicant to an instructor, and updates the application timestamp.
     * </p>
     *
     * @param applicationId the unique identifier of the instructor application to approve
     * @throws BadRequestException if the application is already approved
     */
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

    /**
     * Rejects an instructor application.
     * <p>
     * Changes the status of the specified instructor application to {@link ApplicationStatus#REJECTED},
     * records the reason for rejection, and updates the application timestamp.
     * </p>
     *
     * @param applicationId the unique identifier of the instructor application to reject
     * @param reason        the reason for rejecting the application
     * @throws BadRequestException if the application is already rejected
     */
    @Transactional
    public void rejectInstructorApp(UUID applicationId, String reason) {
        var application = instructorAppService.findById(applicationId);
        if (application.getStatus() == ApplicationStatus.REJECTED) {
            throw new BadRequestException("Application already rejected");
        }
        updateInstructorAppStatus(application, ApplicationStatus.REJECTED, reason);
        // TODO: notify user
    }

    /**
     * Updates the status of an instructor application.
     * <p>
     * Sets the new status, admin message, and reviewed timestamp for the specified application.
     * </p>
     *
     * @param application the {@link InstructorApp} entity to update
     * @param status      the new {@link ApplicationStatus} to set
     * @param reason      the admin message or reason for the status change (can be {@code null})
     */
    private void updateInstructorAppStatus(InstructorApp application, ApplicationStatus status, String reason) {
        application.setStatus(status);
        application.setAdminMessage(reason);
        application.setReviewedAt(LocalDateTime.now());
        instructorAppService.update(application);
    }

    /**
     * Blocks a user from accessing the platform.
     * <p>
     * Verifies that the requesting user is an admin, ensures the target user is not an admin,
     * and proceeds to block the specified user.
     * </p>
     *
     * @param admin  the {@link User} entity representing the admin performing the block operation
     * @param userId the unique identifier of the user to block
     * @throws UserIsNotAdminException      if the requesting user is not an admin
     * @throws AdminCantBeBlockedException if an attempt is made to block another admin
     */
    public void blockUser(@NonNull User admin, @NonNull String userId) {
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UserIsNotAdminException(admin.getId());
        }
        var user = userService.findById(userId);
        if (user.getRole() == UserRole.ADMIN) {
            throw new AdminCantBeBlockedException(user.getId());
        }
        userService.blockUser(user);
    }
}
