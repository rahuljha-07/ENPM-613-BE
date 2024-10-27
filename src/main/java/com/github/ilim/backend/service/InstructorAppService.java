package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.repo.InstructorAppRepo;
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

    public void saveInstructorApp(InstructorApp application) {
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
