package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import com.github.ilim.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public List<User> getAll() {
        return userRepo.findAll();
    }

    public User findById(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public User create(@NonNull User user) {
        return userRepo.save(user);
    }

    @Transactional
    public void promoteToInstructor(InstructorApp application) {
        var user = findById(application.getUserId());
        user.setProfileImageUrl(application.getProfileImageUrl());
        user.setBio(application.getInstructorBio());
        user.setTitle(application.getInstructorTitle());
        user.setRole(UserRole.INSTRUCTOR);
        userRepo.save(user);
    }

    public List<User> findByRole(UserRole role) {
        return userRepo.findByRole(role);
    }
}
