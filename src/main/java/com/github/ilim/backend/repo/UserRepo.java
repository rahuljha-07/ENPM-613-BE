package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {
    
}
