package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
import com.github.ilim.backend.repo.InstructorAppRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorAppService {
    private final InstructorAppRepo appRepo;

    public boolean existPendingApplicationForUser(String userId) {
        return appRepo.findByUserId(userId).stream()
            .anyMatch(application -> application.getStatus() == ApplicationStatus.PENDING);
    }

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

    public List<InstructorApp> findByUserId(String id) {
        return appRepo.findByUserId(id);
    }

    public InstructorApp findById(String id) {
        return appRepo.findById(UUID.fromString(id))
            .orElseThrow(() -> new InstructorAppNotFoundException(id));
    }

    public List<InstructorApp> findAll() {
        return appRepo.findAll();
    }

    public List<InstructorApp> findPendingApplications() {
        return appRepo.findByStatus(ApplicationStatus.PENDING);
    }

    public void update(InstructorApp application) {
        appRepo.save(application);
    }

}
