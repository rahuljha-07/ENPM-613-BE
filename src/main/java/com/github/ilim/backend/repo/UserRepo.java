package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
  
    List<User> findByRole(UserRole role, Sort sort);
  
}
