package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApplication;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.repo.InstructorApplicationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorApplicationService {
    private final InstructorApplicationRepo appRepo;

    public boolean existPendingApplicationForUser(String userId) {
        return appRepo.findByUserId(userId).stream()
            .anyMatch(application -> application.getStatus() == ApplicationStatus.PENDING);
    }

    public void saveInstructorApplication(InstructorApplication application) {
        appRepo.save(application);
    }

    public List<InstructorApplication> findByUserId(String id) {
        return appRepo.findByUserId(id);
    }

    public InstructorApplication findById(UUID id) {
        return appRepo.findById(id)
            .orElseThrow(() -> new InstructorAppNotFoundException(id.toString()));
    }

    public List<InstructorApplication> findAll() {
        return appRepo.findAll();
    }

    public List<InstructorApplication> findPendingApplications() {
        return appRepo.findByStatus(ApplicationStatus.PENDING);
    }

    public void update(InstructorApplication application) {
        appRepo.save(application);
    }

}
