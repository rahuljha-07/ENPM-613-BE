package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.dto.SupportIssueDto;
import com.github.ilim.backend.entity.User;
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
        emailDto.setToAddress(supportEmail);
        emailDto.setSubject(subject);
        emailDto.setContent(emailContent);

        emailSenderService.sendEmail(emailDto);
        logger.info("Support issue email sent successfully to " + supportEmail);
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
