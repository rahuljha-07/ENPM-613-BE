package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.enums.ApplicationStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstructorAppRepo extends JpaRepository<InstructorApp, UUID> {

    List<InstructorApp> findByStatus(ApplicationStatus status, Sort sort);

    List<InstructorApp> findByUserId(String userId, Sort sort);

}