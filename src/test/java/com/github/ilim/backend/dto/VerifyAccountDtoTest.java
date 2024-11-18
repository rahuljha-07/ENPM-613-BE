
package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerifyAccountDtoTest {

    @Test
    void testVerifyAccountDtoFields() {
        VerifyAccountDto dto = new VerifyAccountDto();
        dto.setEmail("verifyuser@example.com");
        dto.setConfirmationCode("XYZ789");

        assertEquals("verifyuser@example.com", dto.getEmail());
        assertEquals("XYZ789", dto.getConfirmationCode());
    }
}

