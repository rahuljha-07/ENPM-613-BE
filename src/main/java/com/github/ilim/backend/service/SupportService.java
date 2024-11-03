package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.dto.SupportIssueDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.EmailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SupportService {
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final EmailSenderService emailSenderService;

    @Value("${admin.supportEmail}")
    private String supportEmail;

    public void sendSupportIssueEmail(User user, SupportIssueDto supportIssueDto) {
        String subject = "Support Issue from " + user.getName();
        String emailContent = buildEmailContent(user, supportIssueDto);

        EmailDto emailDto = new EmailDto();
        emailDto.setTo(supportEmail);
        emailDto.setSubject(subject);
        emailDto.setBody(emailContent);

        try {
            emailSenderService.sendEmail(emailDto);
            logger.info("Support issue email sent successfully to " + supportEmail);
        } catch (Exception e) {
            logger.warning("Failed to send support issue email: " + e.getMessage());
            throw new EmailSendingException("Failed to send support issue email", e);
        }
    }

    private String buildEmailContent(User user, SupportIssueDto supportIssueDto) {
        return "Support Issue Submitted:\n\n" +
                "User ID: " + user.getId() + "\n" +
                "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Role: " + user.getRole() + "\n" +
                "Date: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "\n\n" +
                "Title: " + supportIssueDto.getTitle() + "\n\n" +
                "Message:\n" + supportIssueDto.getDescription() + "\n";
    }
}
