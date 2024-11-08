package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepo extends JpaRepository<User, String> {

    List<User> findByRole(UserRole role);
}
