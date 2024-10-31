package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepo extends JpaRepository<Course, UUID> {
}
