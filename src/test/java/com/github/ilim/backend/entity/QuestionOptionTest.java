package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.QuestionOptionDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionOptionTest {

    @Test
    void testFromDto() {
        QuestionOptionDto dto = new QuestionOptionDto();
        dto.setText("Option A");
        dto.setIsCorrect(true);

        QuestionOption option = QuestionOption.from(dto);

        assertEquals(dto.getText(), option.getText());
        assertEquals(dto.getIsCorrect(), option.getIsCorrect());
    }
}
