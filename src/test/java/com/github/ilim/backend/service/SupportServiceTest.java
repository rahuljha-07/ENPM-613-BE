package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.dto.SupportIssueDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PriorityLevel;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminEmailException;
import com.github.ilim.backend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
public class SupportServiceTest {

    private SupportService supportService;

    @MockBean
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepo userRepo;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    private User student;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        supportService = new SupportService(emailSenderService, new UserService(userRepo));

        // Create student
        student = new User();
        student.setId("student123");
        student.setEmail("student@example.com");
        student.setName("Student Name");
        student.setRole(UserRole.STUDENT);
        student.setBirthdate(LocalDateTime.now().toLocalDate());
        student = userRepo.save(student);

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
    void testSendSupportIssueEmail_Success() {
        SupportIssueDto issueDto = new SupportIssueDto();
        issueDto.setTitle("Issue Title");
        issueDto.setDescription("Issue Description");
        issueDto.setPriority(PriorityLevel.HIGH);

        supportService.sendSupportIssueEmail(student, issueDto);

        verify(emailSenderService, times(1)).sendEmail(any(EmailDto.class));
    }

    @Test
    void testSendSupportIssueEmail_NoAdmin() {
        userRepo.delete(admin);

        SupportIssueDto issueDto = new SupportIssueDto();
        issueDto.setTitle("Issue Title");
        issueDto.setDescription("Issue Description");
        issueDto.setPriority(PriorityLevel.HIGH);

        assertThrows(AdminEmailException.class, () -> {
            supportService.sendSupportIssueEmail(student, issueDto);
        });
    }
}
