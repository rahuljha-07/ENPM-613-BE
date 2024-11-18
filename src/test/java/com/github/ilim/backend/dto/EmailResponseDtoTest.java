package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailResponseDtoTest {

    @Test
    void testEmailResponseDtoFields() {
        EmailResponseDto dto = new EmailResponseDto();
        dto.setMessage("Email sent successfully.");
        dto.setStatus(200);

        assertEquals("Email sent successfully.", dto.getMessage());
        assertEquals(200, dto.getStatus());
    }
}
