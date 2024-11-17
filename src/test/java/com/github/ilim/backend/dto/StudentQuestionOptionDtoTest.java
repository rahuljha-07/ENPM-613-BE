package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class StudentQuestionOptionDtoTest {

    @Test
    void testFrom() {
        // Create Question
        Question question = new Question();
        UUID questionId = UUID.randomUUID();
        question.setId(questionId);

        // Create QuestionOption
        QuestionOption option = new QuestionOption();
        UUID optionId = UUID.randomUUID();
        option.setId(optionId);
        option.setQuestion(question);
        option.setText("Option Text");
        option.setOrderIndex(1);

        StudentQuestionOptionDto dto = StudentQuestionOptionDto.from(option);

        assertEquals(optionId, dto.id());
        assertEquals(questionId, dto.questionId());
        assertEquals("Option Text", dto.text());
        assertEquals(1, dto.orderIndex());
    }
}
