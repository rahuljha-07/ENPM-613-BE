package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class QuestionOptionDtoTest {

    @Test
    void testQuestionOptionDtoFields() {
        QuestionOptionDto dto = new QuestionOptionDto();
        UUID optionId = UUID.randomUUID();
        dto.setId(optionId);
        dto.setText("Option A");
        dto.setIsCorrect(true);

        assertEquals(optionId, dto.getId());
        assertEquals("Option A", dto.getText());
        assertTrue(dto.getIsCorrect());
    }
}
