package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.InstructorAppDto;
import com.github.ilim.backend.entity.AuditEntity;
import com.github.ilim.backend.entity.InstructorApp;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.ApplicationStatus;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UnknownApplicationStatusException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import({InstructorAppService.class})
public class InstructorAppServiceTest {

    @Autowired
    private InstructorAppService instructorAppService;

    @Autowired
    private InstructorAppRepo appRepo;

    @Autowired
    private UserRepo userRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User studentUser;
    private User instructorUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create student user
        studentUser = new User();
        studentUser.setId(UUID.randomUUID().toString());
        studentUser.setEmail("student@example.com");
        studentUser.setName("Student User");
        studentUser.setRole(UserRole.STUDENT);
        studentUser.setBirthdate(LocalDateTime.now().toLocalDate());
        studentUser = userRepo.save(studentUser);

        // Create instructor user
        instructorUser = new User();
        instructorUser.setId(UUID.randomUUID().toString());
        instructorUser.setEmail("instructor@example.com");
        instructorUser.setName("Instructor User");
        instructorUser.setRole(UserRole.INSTRUCTOR);
        instructorUser.setBirthdate(LocalDateTime.now().toLocalDate());
        instructorUser = userRepo.save(instructorUser);

        // Create admin user
        adminUser = new User();
        adminUser.setId(UUID.randomUUID().toString());
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setBirthdate(LocalDateTime.now().toLocalDate());
        adminUser = userRepo.save(adminUser);
    }

    @Test
    @Transactional
    void testSaveInstructorApp_Success() {
        InstructorAppDto appDto = new InstructorAppDto();
        appDto.setInstructorBio("I have 5 years of teaching experience.");
        // Populate other necessary fields

        instructorAppService.saveInstructorApp(studentUser, appDto);

        List<InstructorApp> applications = appRepo.findByUserId(studentUser.getId(), AuditEntity.SORT_BY_CREATED_AT_DESC);
        assertEquals(1, applications.size());
        InstructorApp application = applications.get(0);
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertEquals(studentUser.getId(), application.getUserId());
        assertEquals("I have 5 years of teaching experience.", application.getInstructorBio());
    }

    @Test
    void testSaveInstructorApp_UserAlreadyInstructor() {
        InstructorAppDto appDto = new InstructorAppDto();
        appDto.setInstructorBio("I have 5 years of teaching experience.");

        assertThrows(UserAlreadyInstructorException.class, () -> {
            instructorAppService.saveInstructorApp(instructorUser, appDto);
        });
    }

    @Test
    void testSaveInstructorApp_AdminCannotBeInstructor() {
        InstructorAppDto appDto = new InstructorAppDto();
        appDto.setInstructorBio("I have 5 years of teaching experience.");

        assertThrows(AdminCannotBeInstructorException.class, () -> {
            instructorAppService.saveInstructorApp(adminUser, appDto);
        });
    }

    @Test
    @Transactional
    void testSaveInstructorApp_PendingApplicationExists() {
        InstructorApp existingApp = new InstructorApp();
        existingApp.setId(UUID.randomUUID());
        existingApp.setUserId(studentUser.getId());
        existingApp.setStatus(ApplicationStatus.PENDING);
        appRepo.save(existingApp);

        InstructorAppDto appDto = new InstructorAppDto();
        appDto.setInstructorBio("I have 5 years of teaching experience.");

        assertThrows(InstructorAppAlreadyExistsException.class, () -> {
            instructorAppService.saveInstructorApp(studentUser, appDto);
        });
    }

    @Test
    void testFindByUserId_Success() {
        InstructorApp application = new InstructorApp();
        application.setId(UUID.randomUUID());
        application.setUserId(studentUser.getId());
        application.setStatus(ApplicationStatus.APPROVED);
        appRepo.save(application);

        List<InstructorApp> applications = instructorAppService.findByUserId(studentUser.getId(), null);

        assertEquals(1, applications.size());
        assertEquals(ApplicationStatus.APPROVED, applications.get(0).getStatus());
    }

    @Test
    void testFindByUserId_WithStatus() {
        InstructorApp application1 = new InstructorApp();
        application1.setId(UUID.randomUUID());
        application1.setUserId(studentUser.getId());
        application1.setStatus(ApplicationStatus.APPROVED);
        appRepo.save(application1);

        InstructorApp application2 = new InstructorApp();
        application2.setId(UUID.randomUUID());
        application2.setUserId(studentUser.getId());
        application2.setStatus(ApplicationStatus.PENDING);
        appRepo.save(application2);

        List<InstructorApp> pendingApps = instructorAppService.findByUserId(studentUser.getId(), "pending");

        assertEquals(1, pendingApps.size());
        assertEquals(ApplicationStatus.PENDING, pendingApps.get(0).getStatus());
    }

    @Test
    void testFindByUserId_InvalidStatus() {
        assertThrows(UnknownApplicationStatusException.class, () -> {
            instructorAppService.findByUserId(studentUser.getId(), "unknown_status");
        });
    }

    @Test
    void testFindById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(InstructorAppNotFoundException.class, () -> {
            instructorAppService.findById(nonExistentId);
        });
    }

}
