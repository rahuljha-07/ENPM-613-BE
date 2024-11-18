package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.QuestionDto;
import com.github.ilim.backend.enums.QuestionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionTest {

    @Test
    void testFromDto() {
        QuestionDto dto = new QuestionDto();
        dto.setText("What is Java?");
        dto.setType(QuestionType.MULTIPLE_CHOICE);
        dto.setPoints(new BigDecimal("10"));

        Question question = Question.from(dto);

        assertEquals(dto.getText(), question.getText());
        assertEquals(dto.getType(), question.getType());
        assertEquals(dto.getPoints(), question.getPoints());
    }

    @Test
    void testGetQuizId() {
        Quiz quiz = new Quiz();
        quiz.setId(UUID.randomUUID());

        Question question = new Question();
        question.setQuiz(quiz);

        assertEquals(quiz.getId(), question.getQuizId());
    }
}
