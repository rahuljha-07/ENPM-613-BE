package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ModuleRepo extends JpaRepository<Module, UUID> {

}
