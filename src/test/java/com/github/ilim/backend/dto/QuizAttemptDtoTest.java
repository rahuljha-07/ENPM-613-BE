
package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class QuizAttemptDtoTest {

    @Test
    void testQuizAttemptDtoFields() {
        QuizAttemptDto dto = new QuizAttemptDto();
        UUID attemptId = UUID.randomUUID();
        dto.setId(attemptId);
        UUID quizId = UUID.randomUUID();
        dto.setQuizId(quizId);
        dto.setScore(85);
        dto.setPassed(true);

        UserAnswerDto answer1 = new UserAnswerDto();
        answer1.setQuestionId(UUID.randomUUID());
        answer1.setSelectedOptionIds(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        UserAnswerDto answer2 = new UserAnswerDto();
        answer2.setQuestionId(UUID.randomUUID());
        answer2.setSelectedOptionIds(Arrays.asList(UUID.randomUUID()));

        dto.setAnswers(Arrays.asList(answer1, answer2));

        assertEquals(attemptId, dto.getId());
        assertEquals(quizId, dto.getQuizId());
        assertEquals(85, dto.getScore());
        assertTrue(dto.getPassed());
        assertEquals(2, dto.getAnswers().size());
    }
}

