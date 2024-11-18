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

/**
 * Service class responsible for sending emails.
 * <p>
 * Utilizes a {@link WebClient} to communicate with an external email service.
 * </p>
 *
 * @see WebClient
 */
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final Logger logger = Logger.getLogger(EmailSenderService.class.getName());

    private final WebClient webClient;

    @Value("${emailServiceUrl}")
    private String emailServiceUrl;

    /**
     * Sends an email based on the provided {@link EmailDto}.
     * <p>
     * Constructs and sends an HTTP POST request to the configured email service URL with the email details.
     * Logs the outcome and handles any exceptions that may occur during the process.
     * </p>
     *
     * @param emailDto the data transfer object containing email details such as recipient, subject, and body
     * @throws EmailSendingException if the email service returns a non-OK status or if an error occurs during sending
     */
    public void sendEmail(EmailDto emailDto) {
        try {
            EmailResponseDto response = webClient
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
