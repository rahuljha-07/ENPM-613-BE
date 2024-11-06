package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
}
