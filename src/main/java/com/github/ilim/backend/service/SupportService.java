package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.dto.SupportIssueDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.AdminEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final EmailSenderService emailSenderService;

    private final UserService userService;

    public void sendSupportIssueEmail(User user, SupportIssueDto supportIssueDto) {
        String subject = "Support Issue from " + user.getName();
        String emailContent = buildEmailContent(user, supportIssueDto);

        List<User> adminUsers = userService.findByRole(UserRole.ADMIN);

        if (adminUsers.isEmpty()) {
            throw new AdminEmailException("No admin users found to send support email.");
        }

        List<String> adminEmails = adminUsers.stream()
                .map(User::getEmail)
                .filter(email -> email != null && !email.isEmpty())
                .collect(Collectors.toList());

        String toAddress = adminEmails.getFirst();
        List<String> ccAddresses = adminEmails.size() > 1 ? adminEmails.subList(1, adminEmails.size()) : List.of();

        EmailDto emailDto = new EmailDto();
        emailDto.setToAddress(toAddress);
        emailDto.setCcAddresses(ccAddresses);
        emailDto.setSubject(subject);
        emailDto.setContent(emailContent);

        emailSenderService.sendEmail(emailDto);
        logger.info("Support issue email sent successfully to " + toAddress);
    }

    private String buildEmailContent(User user, SupportIssueDto supportIssueDto) {
        return "Support Issue Submitted:\n\n" +
                "User ID: " + user.getId() + "\n" +
                "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Role: " + user.getRole() + "\n" +
                "Date: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "\n\n" +
                "Title: " + supportIssueDto.getTitle() + "\n" +
                "Priority: " + supportIssueDto.getPriority() + "\n\n" +
                "Message:\n" + supportIssueDto.getDescription() + "\n";
    }
}
