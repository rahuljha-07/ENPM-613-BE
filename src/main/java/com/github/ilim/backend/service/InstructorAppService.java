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

@Service
@RequiredArgsConstructor
public class InstructorAppService {
    private final InstructorAppRepo appRepo;

    public boolean existPendingApplicationForUser(String userId) {
        return appRepo.findByUserId(userId, InstructorApp.SORT_BY_CREATED_AT_DESC).stream()
            .anyMatch(application -> application.getStatus() == ApplicationStatus.PENDING);
    }

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

    public InstructorApp findById(UUID id) {
        return appRepo.findById(id)
            .orElseThrow(() -> new InstructorAppNotFoundException(id));
    }

    public List<InstructorApp> findAll() {
        return appRepo.findAll(InstructorApp.SORT_BY_CREATED_AT_DESC);
    }

    public List<InstructorApp> findPendingApplications() {
        return appRepo.findByStatus(ApplicationStatus.PENDING, InstructorApp.SORT_BY_CREATED_AT_DESC);
    }

    @Transactional
    public void update(InstructorApp application) {
        appRepo.save(application);
    }

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

