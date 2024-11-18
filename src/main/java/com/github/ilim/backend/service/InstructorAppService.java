package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UnknownApplicationStatusException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
import com.github.ilim.backend.repo.InstructorAppRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for managing instructor applications.
 * <p>
 * Provides functionalities such as saving new instructor applications,
 * retrieving applications based on user ID and status, and updating application statuses.
 * </p>
 *
 * @see InstructorAppRepo
 */
@Service
@RequiredArgsConstructor
public class InstructorAppService {
    private final InstructorAppRepo appRepo;

    /**
     * Checks if a pending instructor application exists for a given user ID.
     *
     * @param userId the unique identifier of the user
     * @return {@code true} if a pending application exists, {@code false} otherwise
     */
    public boolean existPendingApplicationForUser(String userId) {
        return appRepo.findByUserId(userId, InstructorApp.SORT_BY_CREATED_AT_DESC).stream()
            .anyMatch(application -> application.getStatus() == ApplicationStatus.PENDING);
    }

    /**
     * Saves a new instructor application for a user.
     * <p>
     * Validates the user's current role and existing applications before saving a new application.
     * </p>
     *
     * @param user the {@link User} submitting the instructor application
     * @param dto  the data transfer object containing application details
     * @throws UserAlreadyInstructorException       if the user is already an instructor
     * @throws AdminCannotBeInstructorException     if the user is an admin and cannot become an instructor
     * @throws InstructorAppAlreadyExistsException if a pending application already exists for the user
     */
    @Transactional
    public void saveInstructorApp(User user, @Valid InstructorAppDto dto) {
        if (user.getRole() == UserRole.INSTRUCTOR) {
            throw new UserAlreadyInstructorException(user.getId());
        }
        else if (user.getRole() == UserRole.ADMIN) {
            throw new AdminCannotBeInstructorException(user.getId());
        }
        else if (existPendingApplicationForUser(user.getId())) {
            throw new InstructorAppAlreadyExistsException(user.getId());
        }
        var application = InstructorApp.from(dto);
        application.setUserId(user.getId());
        appRepo.save(application);
    }

    /**
     * Retrieves a list of instructor applications for a specific user ID and optional status filter.
     *
     * @param id           the unique identifier of the user
     * @param statusString the status filter for applications (e.g., "PENDING", "APPROVED", "REJECTED")
     * @return a list of {@link InstructorApp} matching the criteria
     * @throws UnknownApplicationStatusException if the provided status string does not match any valid status
     */
    public List<InstructorApp> findByUserId(String id, @Nullable String statusString) {
        var applications = appRepo.findByUserId(id, InstructorApp.SORT_BY_CREATED_AT_DESC);
        if (statusString == null) {
            return applications;
        }
        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownApplicationStatusException(statusString);
        }
        return applications.stream()
            .filter(app -> app.getStatus().equals(status))
            .toList();
    }

    /**
     * Retrieves an instructor application by its unique identifier.
     *
     * @param id the unique identifier of the instructor application
     * @return the {@link InstructorApp} corresponding to the provided ID
     * @throws InstructorAppNotFoundException if no application is found with the given ID
     */
    public InstructorApp findById(UUID id) {
        return appRepo.findById(id)
            .orElseThrow(() -> new InstructorAppNotFoundException(id));
    }

    /**
     * Retrieves all instructor applications.
     *
     * @return a list of all {@link InstructorApp} entries
     */
    public List<InstructorApp> findAll() {
        return appRepo.findAll(InstructorApp.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Retrieves all pending instructor applications.
     *
     * @return a list of {@link InstructorApp} entries with {@link ApplicationStatus#PENDING} status
     */
    public List<InstructorApp> findPendingApplications() {
        return appRepo.findByStatus(ApplicationStatus.PENDING, InstructorApp.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Updates an existing instructor application.
     *
     * @param application the {@link InstructorApp} entity to update
     */
    @Transactional
    public void update(InstructorApp application) {
        appRepo.save(application);
    }

    /**
     * Cancels a pending instructor application for a user.
     * <p>
     * Updates the status of the latest pending application to {@link ApplicationStatus#REJECTED}
     * with a predefined admin message.
     * </p>
     *
     * @param user the {@link User} requesting the cancellation of their pending application
     * @throws InstructorAppNotFoundException if no pending application is found for the user
     */
    @Transactional
    public void cancelPendingInstructorApplication(User user) {
        var applications = findByUserId(user.getId(), ApplicationStatus.PENDING.name());
        if (applications.isEmpty()) {
            throw new InstructorAppNotFoundException(null);
        }
        var application = applications.getFirst();
        application.setStatus(ApplicationStatus.REJECTED);
        application.setAdminMessage("Cancelled by the user");
        appRepo.save(application);
    }
}
