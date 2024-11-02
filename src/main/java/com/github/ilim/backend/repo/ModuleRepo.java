package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ModuleRepo extends JpaRepository<CourseModule, UUID> {

}
