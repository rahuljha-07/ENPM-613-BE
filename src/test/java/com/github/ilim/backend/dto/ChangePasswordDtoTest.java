package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordDtoTest {

    @Test
    void testChangePasswordDtoFields() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("oldPass123");
        dto.setNewPassword("newPass456");

        assertEquals("oldPass123", dto.getOldPassword());
        assertEquals("newPass456", dto.getNewPassword());
    }
}
