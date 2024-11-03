package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final WebClient.Builder webClientBuilder;

    public void sendEmail(EmailDto emailDto) {
        String emailServiceUrl = "http://email-service/send-email";

        webClientBuilder.build()
                .post()
                .uri(emailServiceUrl)
                .bodyValue(emailDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(aVoid -> System.out.println("Email sent successfully."))
                .doOnError(error -> System.err.println("Failed to send email: " + error.getMessage()))
                .subscribe();
    }
}

