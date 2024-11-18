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

/**
 * Service class responsible for processing payments.
 * <p>
 * Utilizes a {@link WebClient} to communicate with an external payment service.
 * </p>
 *
 * @see WebClient
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());

    private final WebClient webClient;

    @Value("${paymentServiceUrl}")
    private String paymentServiceUrl;

    /**
     * Creates a checkout session for a payment request.
     * <p>
     * Sends a payment request to the payment service and retrieves the checkout URL for the transaction.
     * </p>
     *
     * @param paymentRequestDto the data transfer object containing payment details such as user, course, and currency
     * @return the checkout URL as a {@link String} where the user can complete the payment
     * @throws PaymentProcessingException if the payment service returns a null response or an error occurs during processing
     */
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
