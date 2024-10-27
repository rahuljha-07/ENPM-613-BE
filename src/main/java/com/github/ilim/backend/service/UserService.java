package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import com.github.ilim.backend.repo.UserRepo;
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

    public User create(@NonNull User user) {
        return userRepo.save(user);
    }

    public void promoteToInstructor(InstructorApp application) {
        var user = findById(application.getUserId());
        user.setProfileImageUrl(application.getProfileImageUrl());
        user.setInstructorBio(application.getInstructorBio());
        user.setInstructorTitle(application.getInstructorTitle());
        user.setRole(UserRole.INSTRUCTOR);
        userRepo.save(user);
    }

//    public User update(String userId,@NonNull User updatedUser) {
//        var user = findById(userId);
//        user.setName(updatedUser.getName());
//        user.setBirthdate(updatedUser.getBirthdate());
//        user.setProfileImageUrl(updatedUser.getProfileImageUrl());
//        // Do not update email or ID
//        return userRepo.save(user);
//    }
}
