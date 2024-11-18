
package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignInDtoTest {

    @Test
    void testSignInDtoFields() {
        SignInDto dto = new SignInDto();
        dto.setEmail("user@example.com");
        dto.setPassword("password123");

        assertEquals("user@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
    }
}

