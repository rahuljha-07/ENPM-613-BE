package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.PaymentRequestDto;
import com.github.ilim.backend.dto.PaymentResponseDto;
import com.github.ilim.backend.exception.exceptions.PaymentProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());

    private final WebClient webClient;

    @Value("${paymentServiceUrl}")
    private String paymentServiceUrl;

    public String createCheckoutSession(PaymentRequestDto paymentRequestDto) {
        try {
            PaymentResponseDto response = webClient
                .post()
                .uri(paymentServiceUrl)
                .bodyValue(paymentRequestDto)
                .retrieve()
                .bodyToMono(PaymentResponseDto.class)
                .block();

            if (response == null) {
                throw new PaymentProcessingException("The response was null");
            }

            logger.log(Level.INFO, "Stripe URL received successfully: {0}", response.getStripeUrl());

            return response.getStripeUrl();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while processing payment", e);
            throw new PaymentProcessingException("Failed to process payment", e);
        }
    }
}
