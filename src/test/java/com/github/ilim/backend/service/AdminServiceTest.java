package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCantBeBlockedException;
import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.UserIsNotAdminException;
import com.github.ilim.backend.repo.InstructorAppRepo;
import com.github.ilim.backend.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import({AdminService.class, InstructorAppService.class, UserService.class})
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private InstructorAppService instructorAppService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private InstructorAppRepo instructorAppRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User adminUser;
    private User studentUser;
    private InstructorApp instructorApp;

    @BeforeEach
    void setUp() {
        // Create admin user
        adminUser = new User();
        adminUser.setId(UUID.randomUUID().toString());
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        adminUser = userRepo.save(adminUser);

        // Create student user
        studentUser = new User();
        studentUser.setId(UUID.randomUUID().toString());
        studentUser.setEmail("student@example.com");
        studentUser.setName("Student User");
        studentUser.setRole(UserRole.STUDENT);
        studentUser = userRepo.save(studentUser);

        // Create instructor application
        instructorApp = new InstructorApp();
        instructorApp.setId(UUID.randomUUID());
        instructorApp.setUserId(studentUser.getId());
        instructorApp.setStatus(ApplicationStatus.PENDING);
        instructorApp = instructorAppRepo.save(instructorApp);
    }

    @Test
    @Transactional
    void testApproveInstructorApp() {
        // Approve instructor application
        adminService.approveInstructorApp(instructorApp.getId());

        // Verify application status is updated
        InstructorApp updatedApp = instructorAppRepo.findById(instructorApp.getId()).orElse(null);
        assertNotNull(updatedApp);
        assertEquals(ApplicationStatus.APPROVED, updatedApp.getStatus());

        // Verify user role is updated to INSTRUCTOR
        User updatedUser = userRepo.findById(studentUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(UserRole.INSTRUCTOR, updatedUser.getRole());
    }

    @Test
    void testApproveAlreadyApprovedApplication() {
        // Set application status to APPROVED
        instructorApp.setStatus(ApplicationStatus.APPROVED);
        instructorAppRepo.save(instructorApp);

        // Attempt to approve again
        assertThrows(BadRequestException.class, () -> {
            adminService.approveInstructorApp(instructorApp.getId());
        });
    }

    @Test
    @Transactional
    void testRejectInstructorApp() {
        String reason = "Insufficient qualifications";
        adminService.rejectInstructorApp(instructorApp.getId(), reason);

        // Verify application status is updated
        InstructorApp updatedApp = instructorAppRepo.findById(instructorApp.getId()).orElse(null);
        assertNotNull(updatedApp);
        assertEquals(ApplicationStatus.REJECTED, updatedApp.getStatus());
        assertEquals(reason, updatedApp.getAdminMessage());

        // Verify user role remains STUDENT
        User updatedUser = userRepo.findById(studentUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals(UserRole.STUDENT, updatedUser.getRole());
    }

    @Test
    void testRejectAlreadyRejectedApplication() {
        // Set application status to REJECTED
        instructorApp.setStatus(ApplicationStatus.REJECTED);
        instructorAppRepo.save(instructorApp);

        // Attempt to reject again
        assertThrows(BadRequestException.class, () -> {
            adminService.rejectInstructorApp(instructorApp.getId(), "Some reason");
        });
    }

    @Test
    @Transactional
    void testBlockUser() {
        // Admin blocks student user
        adminService.blockUser(adminUser, studentUser.getId());

        // Verify user is blocked
        User updatedUser = userRepo.findById(studentUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(updatedUser.isBlocked());
    }

    @Test
    void testBlockUserAsNonAdmin() {
        // Attempt to block user as non-admin
        assertThrows(UserIsNotAdminException.class, () -> {
            adminService.blockUser(studentUser, adminUser.getId());
        });
    }

    @Test
    void testBlockAdminUser() {
        // Attempt to block admin user
        assertThrows(AdminCantBeBlockedException.class, () -> {
            adminService.blockUser(adminUser, adminUser.getId());
        });
    }
}
