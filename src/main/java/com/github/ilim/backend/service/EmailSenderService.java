package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import com.github.ilim.backend.dto.EmailResponseDto;
import com.github.ilim.backend.exception.exceptions.EmailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final Logger logger = Logger.getLogger(EmailSenderService.class.getName());

    private final WebClient.Builder webClientBuilder;

    @Value("${emailServiceUrl}")
    private String emailServiceUrl;

    public void sendEmail(EmailDto emailDto) {
        try {
            EmailResponseDto response = webClientBuilder.build()
                    .post()
                    .uri(emailServiceUrl)
                    .bodyValue(emailDto)
                    .retrieve()
                    .bodyToMono(EmailResponseDto.class)
                    .block();

            if (response == null || response.getStatus() != HttpStatus.OK.value()) {
                throw new EmailSendingException("Email sending failed with status: " +
                        (response != null ? response.getStatus() : "null response"));
            }

            logger.log(Level.INFO, "Email sent successfully with message: {0}", response.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while sending email", e);
            throw new EmailSendingException("Failed to send email", e);
        }
    }
}
