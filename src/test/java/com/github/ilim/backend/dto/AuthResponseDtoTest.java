package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDtoTest {

    @Test
    void testAuthResponseDtoFields() {
        AuthResponseDto dto = new AuthResponseDto();
        dto.setAccessToken("access_token");
        dto.setIdToken("id_token");
        dto.setRefreshToken("refresh_token");
        dto.setExpiresIn(3600);

        assertEquals("access_token", dto.getAccessToken());
        assertEquals("id_token", dto.getIdToken());
        assertEquals("refresh_token", dto.getRefreshToken());
        assertEquals(3600, dto.getExpiresIn());
    }
}
