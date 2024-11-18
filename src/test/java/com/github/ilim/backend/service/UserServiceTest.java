package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.UpdateUserDto;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.CantUpdateBlockedUserException;
import com.github.ilim.backend.exception.exceptions.UserIsAlreadyBlockedException;
import com.github.ilim.backend.exception.exceptions.UserIsNotAdminException;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import com.github.ilim.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserServiceTest {

    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private User instructor;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepo);

        // Create student
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setRole(UserRole.STUDENT);
        student.setBirthdate(LocalDateTime.now().toLocalDate());
        student = userRepo.save(student);

        // Create instructor
        instructor = new User();
        instructor.setId("instructor123");
        instructor.setEmail("instructor@example.com");
        instructor.setName("Instructor Name");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setBirthdate(LocalDateTime.now().toLocalDate());
        instructor = userRepo.save(instructor);

        // Create admin
        admin = new User();
        admin.setId("admin123");
        admin.setEmail("admin@example.com");
        admin.setName("Admin Name");
        admin.setRole(UserRole.ADMIN);
        admin.setBirthdate(LocalDateTime.now().toLocalDate());
        admin = userRepo.save(admin);
    }

    @Test
    void testFindById_Success() {
        User foundUser = userService.findById(student.getId());

        assertNotNull(foundUser);
        assertEquals(student.getId(), foundUser.getId());
    }

    @Test
    void testFindById_NotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.findById("nonexistent123");
        });
    }

    @Test
    @Transactional
    void testPromoteToInstructor_Success() {
        InstructorApp app = new InstructorApp();
        app.setUserId(student.getId());
        app.setInstructorBio("Experienced in teaching");
        app.setInstructorTitle("Senior Instructor");
        app.setProfileImageUrl("http://example.com/profile.jpg");

        userService.promoteToInstructor(app);

        User updatedUser = userRepo.findById(student.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(UserRole.INSTRUCTOR, updatedUser.getRole());
        assertEquals("Experienced in teaching", updatedUser.getBio());
        assertEquals("Senior Instructor", updatedUser.getTitle());
        assertEquals("http://example.com/profile.jpg", updatedUser.getProfileImageUrl());
    }

    @Test
    @Transactional
    void testUpdateFromDto_Success() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setBio("Updated Bio");
        dto.setTitle("Updated Title");
        dto.setProfileImageUrl("http://example.com/new-profile.jpg");

        userService.updateFromDto(student, dto);

        User updatedUser = userRepo.findById(student.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated Bio", updatedUser.getBio());
        assertEquals("Updated Title", updatedUser.getTitle());
        assertEquals("http://example.com/new-profile.jpg", updatedUser.getProfileImageUrl());
    }

    @Test
    void testUpdateFromDto_BlockedUser() {
        student.setBlocked(true);
        userRepo.save(student);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setBio("Updated Bio");

        assertThrows(CantUpdateBlockedUserException.class, () -> {
            userService.updateFromDto(student, dto);
        });
    }

    @Test
    @Transactional
    void testBlockUser_Success() {
        userService.blockUser(student);

        User updatedUser = userRepo.findById(student.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(updatedUser.isBlocked());
    }

    @Test
    void testBlockUser_AlreadyBlocked() {
        student.setBlocked(true);
        userRepo.save(student);

        assertThrows(UserIsAlreadyBlockedException.class, () -> {
            userService.blockUser(student);
        });
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userService.getAll();
        assertNotNull(users);
        assertTrue(users.size() >= 3); // Adjust based on known users
    }

    @Test
    void testFindByIdAsAdmin_AsAdmin() {
        User foundUser = userService.findByIdAsAdmin(admin, student.getId());
        assertNotNull(foundUser);
        assertEquals(student.getId(), foundUser.getId());
    }

    @Test
    void testFindByIdAsAdmin_AsNonAdmin() {
        assertThrows(UserIsNotAdminException.class, () -> {
            userService.findByIdAsAdmin(student, instructor.getId());
        });
    }


}
