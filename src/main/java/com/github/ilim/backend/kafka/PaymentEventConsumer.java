package com.github.ilim.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.enums.PaymentIntentStatus;
import com.github.ilim.backend.service.CoursePurchaseService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component responsible for consuming payment events from Kafka topics.
 * <p>
 * Listens to payment-related Kafka topics, processes incoming payment events,
 * and triggers appropriate actions in the {@link CoursePurchaseService} based on the payment status.
 * </p>
 *
 * @see CoursePurchaseService
 */
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final Logger logger = Logger.getLogger(PaymentEventConsumer.class.getName());

    private final CoursePurchaseService coursePurchaseService;
    private final ObjectMapper objectMapper;

    /**
     * Consumes payment events from the configured Kafka topic.
     * <p>
     * Parses the incoming JSON message into a {@link PaymentEventDto} object,
     * determines the payment intent status, and invokes the corresponding method
     * in {@link CoursePurchaseService} to confirm or reject the purchase.
     * </p>
     *
     * @param record the {@link ConsumerRecord} containing the payment event message
     */
    @KafkaListener(topics = "${kafka.topic.payment}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentEvent(ConsumerRecord<String, String> record) {
        String message = record.value();
        try {
            PaymentEventDto paymentEvent = objectMapper.readValue(message, PaymentEventDto.class);
            var status = PaymentIntentStatus.from(paymentEvent.getStatus());

            if (Objects.requireNonNull(status) == PaymentIntentStatus.SUCCEEDED) {
                coursePurchaseService.confirmPurchase(paymentEvent);
            } else {
                coursePurchaseService.rejectPurchase(paymentEvent, status);
            }
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error parsing payment event message", e);
        }
    }
}
