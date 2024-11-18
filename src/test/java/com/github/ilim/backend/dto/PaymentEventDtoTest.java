package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentEventDtoTest {

    @Test
    void testPaymentEventDtoFields() {
        PaymentEventDto dto = new PaymentEventDto();
        dto.setUserId("user123");
        dto.setCourseId("course456");
        dto.setPaymentId("payment789");
        dto.setPaymentDate("2024-04-27");
        dto.setStatus("COMPLETED");

        assertEquals("user123", dto.getUserId());
        assertEquals("course456", dto.getCourseId());
        assertEquals("payment789", dto.getPaymentId());
        assertEquals("2024-04-27", dto.getPaymentDate());
        assertEquals("COMPLETED", dto.getStatus());
    }
}
