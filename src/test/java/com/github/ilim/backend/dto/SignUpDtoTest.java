package com.github.ilim.backend.dto;

import com.github.ilim.backend.exception.exceptions.MissingBirthdateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SignUpDtoTest {

    @Test
    void testSignUpDtoFields() {
        SignUpDto dto = new SignUpDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("newpassword");
        dto.setName("New User");
        dto.setBirthdate(LocalDate.of(1990, 1, 1));

        assertEquals("newuser@example.com", dto.getEmail());
        assertEquals("newpassword", dto.getPassword());
        assertEquals("New User", dto.getName());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthdate());
    }

    @Test
    void testGetBirthdateString() {
        SignUpDto dto = new SignUpDto();
        dto.setBirthdate(LocalDate.of(1990, 1, 1));

        assertEquals("1990-01-01", dto.getBirthdateString());
    }

    @Test
    void testGetBirthdateStringThrowsException() {
        SignUpDto dto = new SignUpDto();
        dto.setBirthdate(null);

        assertThrows(MissingBirthdateException.class, dto::getBirthdateString);
    }
}
