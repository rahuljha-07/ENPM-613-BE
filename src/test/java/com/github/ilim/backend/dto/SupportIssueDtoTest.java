package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.PriorityLevel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupportIssueDtoTest {

    @Test
    void testSupportIssueDtoFields() {
        SupportIssueDto dto = new SupportIssueDto();
        dto.setTitle("Cannot access course");
        dto.setDescription("I am unable to access the Java course I purchased.");
        dto.setPriority(PriorityLevel.HIGH);

        assertEquals("Cannot access course", dto.getTitle());
        assertEquals("I am unable to access the Java course I purchased.", dto.getDescription());
        assertEquals(PriorityLevel.HIGH, dto.getPriority());
    }

    @Test
    void testDefaultPriority() {
        SupportIssueDto dto = new SupportIssueDto();
        assertEquals(PriorityLevel.LOW, dto.getPriority());
    }
}
