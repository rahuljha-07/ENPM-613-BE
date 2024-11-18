
package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResetPasswordDtoTest {

    @Test
    void testResetPasswordDtoFields() {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setEmail("user@example.com");
        dto.setConfirmationCode("ABC123");
        dto.setNewPassword("newSecurePassword");

        assertEquals("user@example.com", dto.getEmail());
        assertEquals("ABC123", dto.getConfirmationCode());
        assertEquals("newSecurePassword", dto.getNewPassword());
    }
}

