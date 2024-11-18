package com.github.ilim.backend.dto;

import com.github.ilim.backend.enums.QuestionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionDtoTest {

    @Test
    void testQuestionDtoFields() {
        QuestionDto dto = new QuestionDto();
        UUID questionId = UUID.randomUUID();
        dto.setId(questionId);
        dto.setText("What is Java?");
        dto.setType(QuestionType.MULTIPLE_CHOICE);
        dto.setPoints(new BigDecimal("10.0"));

        QuestionOptionDto option1 = new QuestionOptionDto();
        option1.setId(UUID.randomUUID());
        option1.setText("A programming language");
        option1.setIsCorrect(true);

        QuestionOptionDto option2 = new QuestionOptionDto();
        option2.setId(UUID.randomUUID());
        option2.setText("A coffee brand");
        option2.setIsCorrect(false);

        dto.setOptions(Arrays.asList(option1, option2));

        assertEquals(questionId, dto.getId());
        assertEquals("What is Java?", dto.getText());
        assertEquals(QuestionType.MULTIPLE_CHOICE, dto.getType());
        assertEquals(new BigDecimal("10.0"), dto.getPoints());
        assertEquals(2, dto.getOptions().size());
    }
}
