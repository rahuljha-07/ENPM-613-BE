
package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.Question;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuizDtoTest {

    @Test
    void testFrom() {
        // Mock Quiz
        Quiz quiz = mock(Quiz.class);
        UUID quizId = UUID.randomUUID();
        when(quiz.getId()).thenReturn(quizId);
        when(quiz.getTitle()).thenReturn("Java Basics Quiz");
        when(quiz.getDescription()).thenReturn("A quiz on Java fundamentals.");
        when(quiz.getPassingScore()).thenReturn(new BigDecimal("70.0")); 

        // Mock Questions
        Question question1 = mock(Question.class);
        UUID question1Id = UUID.randomUUID();
        when(question1.getId()).thenReturn(question1Id);
        when(question1.getText()).thenReturn("What is JVM?");
        when(question1.getType()).thenReturn(com.github.ilim.backend.enums.QuestionType.MULTIPLE_CHOICE);
        when(question1.getPoints()).thenReturn(new BigDecimal("10.0"));
        when(question1.getOptions()).thenReturn(Arrays.asList());

        when(quiz.getQuestions()).thenReturn(Arrays.asList(question1));

        UserRole role = UserRole.ADMIN;

        QuizDto dto = QuizDto.from(quiz, role);

        assertEquals(quizId, dto.getId());
        assertEquals("Java Basics Quiz", dto.getTitle());
        assertEquals("A quiz on Java fundamentals.", dto.getDescription());
        assertEquals(new BigDecimal("70.0"), dto.getPassingScore());
        assertEquals(1, dto.getQuestions().size());

        QuestionDto qDto = dto.getQuestions().get(0);
        assertEquals(question1Id, qDto.getId());
        assertEquals("What is JVM?", qDto.getText());
        assertEquals(com.github.ilim.backend.enums.QuestionType.MULTIPLE_CHOICE, qDto.getType());
        assertEquals(new BigDecimal("10.0"), qDto.getPoints());
        assertEquals(0, qDto.getOptions().size());
    }
}

