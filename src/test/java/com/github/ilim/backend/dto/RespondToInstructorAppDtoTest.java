
package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RespondToInstructorAppDtoTest {

    @Test
    void testRespondToInstructorAppDtoFields() {
        RespondToInstructorAppDto dto = new RespondToInstructorAppDto();
        UUID appId = UUID.randomUUID();
        dto.setInstructorApplicationId(appId);
        dto.setMessage("Your application has been approved.");

        assertEquals(appId, dto.getInstructorApplicationId());
        assertEquals("Your application has been approved.", dto.getMessage());
    }
}

