package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.UpdateUserDto;
import com.github.ilim.backend.entity.AuditEntity;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.CantUpdateBlockedUserException;
import com.github.ilim.backend.exception.exceptions.UserIsAlreadyBlockedException;
import com.github.ilim.backend.exception.exceptions.UserIsNotAdminException;
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
        return userRepo.findAll(AuditEntity.SORT_BY_CREATED_AT_DESC);
    }

    public User findById(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User findByIdAsAdmin(@NonNull User admin, String userId) {
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UserIsNotAdminException(admin.getId());
        }
        return findById(userId);
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

    @Transactional
    public void demoteToStudent(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new BadRequestException("User(%s) is not instructor!".formatted(user.getId()));
        }
        user.setRole(UserRole.STUDENT);
        userRepo.save(user);
    }


    public void updateFromDto(@NonNull User user, @NonNull UpdateUserDto dto) {
        assertUserIsActive(user);
        user.setBio(dto.getBio());
        user.setTitle(dto.getTitle());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        // Core attributes in cognito cannot be updated in the current implementation
        userRepo.save(user);
    }

    private void assertUserIsActive(@NonNull User user) {
        if (user.isBlocked()) {
            throw new CantUpdateBlockedUserException(user.getId());
        }
    }

    public boolean isUserBlockedByEmail(String email) {
        var user = userRepo.findByEmail(email);
        return user.isPresent() && user.get().isBlocked();
    }

    public void blockUser(User user) {
        if (user.isBlocked()) {
            throw new UserIsAlreadyBlockedException(user.getId());
        }
        user.setBlocked(true);
        userRepo.save(user);
    }

    public List<User> findByRole(UserRole role) {
        return userRepo.findByRole(role, AuditEntity.SORT_BY_CREATED_AT_DESC);

    }
}
