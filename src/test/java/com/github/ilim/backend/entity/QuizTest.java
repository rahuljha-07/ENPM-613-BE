package com.github.ilim.backend.entity;

import com.github.ilim.backend.dto.QuizDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizTest {

    @Test
    void testFromDto() {
        QuizDto dto = new QuizDto();
        dto.setTitle("Java Basics Quiz");
        dto.setDescription("Test your Java knowledge");
        dto.setPassingScore(new BigDecimal("70"));

        Quiz quiz = Quiz.from(dto);

        assertEquals(dto.getTitle(), quiz.getTitle());
        assertEquals(dto.getDescription(), quiz.getDescription());
        assertEquals(dto.getPassingScore(), quiz.getPassingScore());
    }

    @Test
    void testUpdateFromDto() {
        QuizDto dto = new QuizDto();
        dto.setTitle("Advanced Java Quiz");
        dto.setDescription("Advanced topics in Java");
        dto.setPassingScore(new BigDecimal("80"));

        Quiz quiz = new Quiz();
        quiz.updateFrom(dto);

        assertEquals(dto.getTitle(), quiz.getTitle());
        assertEquals(dto.getDescription(), quiz.getDescription());
        assertEquals(dto.getPassingScore(), quiz.getPassingScore());
    }
}
