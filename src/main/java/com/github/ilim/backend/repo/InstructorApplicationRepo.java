package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.InstructorApplication;
import com.github.ilim.backend.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstructorApplicationRepo extends JpaRepository<InstructorApplication, UUID> {

    List<InstructorApplication> findByStatus(ApplicationStatus status);

    List<InstructorApplication> findByUserId(String userId);

    boolean existsByUserId(String id);
}
