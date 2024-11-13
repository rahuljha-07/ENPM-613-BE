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

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final Logger logger = Logger.getLogger(PaymentEventConsumer.class.getName());

    private final CoursePurchaseService coursePurchaseService;
    private final ObjectMapper objectMapper;

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
