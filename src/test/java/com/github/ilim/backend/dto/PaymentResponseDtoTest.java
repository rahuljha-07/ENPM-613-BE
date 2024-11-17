package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseDtoTest {

    @Test
    void testPaymentResponseDtoFields() {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setStripeUrl("http://stripe.com/payment/123");

        assertEquals("http://stripe.com/payment/123", dto.getStripeUrl());
    }
}
