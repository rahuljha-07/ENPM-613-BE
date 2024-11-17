package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateUserDtoTest {

    @Test
    void testUpdateUserDtoFields() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setProfileImageUrl("http://example.com/new_profile.jpg");
        dto.setTitle("Senior Developer");
        dto.setBio("Updated bio for the user.");

        assertEquals("http://example.com/new_profile.jpg", dto.getProfileImageUrl());
        assertEquals("Senior Developer", dto.getTitle());
        assertEquals("Updated bio for the user.", dto.getBio());
    }
}
