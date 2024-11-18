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

/**
 * Service class responsible for managing user-related operations.
 * <p>
 * Provides functionalities such as retrieving users, promoting/demoting roles,
 * blocking users, and updating user profiles.
 * </p>
 *
 * @see UserRepo
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    /**
     * Retrieves all users sorted by their creation date in descending order.
     *
     * @return a list of all {@link User} entities
     */
    public List<User> getAll() {
        return userRepo.findAll(AuditEntity.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Finds a user by their unique identifier.
     *
     * @param userId the unique identifier of the user
     * @return the {@link User} entity corresponding to the provided ID
     * @throws UserNotFoundException if no user is found with the given ID
     */
    public User findById(String userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Finds a user by their unique identifier as an admin.
     *
     * @param admin  the {@link User} entity representing the admin
     * @param userId the unique identifier of the user to find
     * @return the {@link User} entity corresponding to the provided ID
     * @throws UserIsNotAdminException if the requesting user is not an admin
     * @throws UserNotFoundException   if no user is found with the given ID
     */
    public User findByIdAsAdmin(@NonNull User admin, String userId) {
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UserIsNotAdminException(admin.getId());
        }
        return findById(userId);
    }

    /**
     * Creates a new user and saves it to the repository.
     *
     * @param user the {@link User} entity to create
     * @return the created {@link User} entity
     */
    @Transactional
    public User create(@NonNull User user) {
        return userRepo.save(user);
    }

    /**
     * Promotes a user to an instructor based on the provided {@link InstructorApp}.
     * <p>
     * Updates the user's profile image URL, bio, title, and role to {@link UserRole#INSTRUCTOR}.
     * </p>
     *
     * @param application the {@link InstructorApp} containing promotion details
     */
    @Transactional
    public void promoteToInstructor(InstructorApp application) {
        var user = findById(application.getUserId());
        user.setProfileImageUrl(application.getProfileImageUrl());
        user.setBio(application.getInstructorBio());
        user.setTitle(application.getInstructorTitle());
        user.setRole(UserRole.INSTRUCTOR);
        userRepo.save(user);
    }

    /**
     * Demotes a user from an instructor to a student.
     *
     * @param user the {@link User} entity to demote
     * @throws BadRequestException if the user is not currently an instructor
     */
    @Transactional
    public void demoteToStudent(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new BadRequestException("User(%s) is not instructor!".formatted(user.getId()));
        }
        user.setRole(UserRole.STUDENT);
        userRepo.save(user);
    }

    /**
     * Updates a user's profile based on the provided {@link UpdateUserDto}.
     *
     * @param user the {@link User} entity to update
     * @param dto  the data transfer object containing updated profile details
     * @throws CantUpdateBlockedUserException if the user is blocked and cannot be updated
     */
    public void updateFromDto(@NonNull User user, @NonNull UpdateUserDto dto) {
        assertUserIsActive(user);
        user.setBio(dto.getBio());
        user.setTitle(dto.getTitle());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        // Core attributes in cognito cannot be updated in the current implementation
        userRepo.save(user);
    }

    /**
     * Asserts that a user is active (not blocked) before allowing updates.
     *
     * @param user the {@link User} entity to check
     * @throws CantUpdateBlockedUserException if the user is blocked
     */
    private void assertUserIsActive(@NonNull User user) {
        if (user.isBlocked()) {
            throw new CantUpdateBlockedUserException(user.getId());
        }
    }

    /**
     * Checks if a user is blocked based on their email address.
     *
     * @param email the email address of the user
     * @return {@code true} if the user is blocked, {@code false} otherwise
     */
    public boolean isUserBlockedByEmail(String email) {
        var user = userRepo.findByEmail(email);
        return user.isPresent() && user.get().isBlocked();
    }

    /**
     * Blocks a user, preventing them from performing certain actions.
     *
     * @param user the {@link User} entity to block
     * @throws UserIsAlreadyBlockedException if the user is already blocked
     */
    public void blockUser(User user) {
        if (user.isBlocked()) {
            throw new UserIsAlreadyBlockedException(user.getId());
        }
        user.setBlocked(true);
        userRepo.save(user);
    }

    /**
     * Finds users by their role.
     *
     * @param role the {@link UserRole} to filter users by
     * @return a list of {@link User} entities matching the specified role
     */
    public List<User> findByRole(UserRole role) {
        return userRepo.findByRole(role, AuditEntity.SORT_BY_CREATED_AT_DESC);
    }
}
