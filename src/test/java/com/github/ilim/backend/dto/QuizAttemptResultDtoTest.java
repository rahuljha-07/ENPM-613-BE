
package com.github.ilim.backend.dto;

import com.github.ilim.backend.entity.QuizAttempt;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuizAttemptResultDtoTest {

    @Test
    void testFrom() {
        // Mock QuizAttempt
        QuizAttempt attempt = mock(QuizAttempt.class);
        UUID attemptId = UUID.randomUUID();
        when(attempt.getId()).thenReturn(attemptId);
        when(attempt.getUserScore()).thenReturn(new BigDecimal("85.0"));
        when(attempt.getTotalScore()).thenReturn(new BigDecimal("100.0"));
        when(attempt.isPassed()).thenReturn(true);

        QuizAttemptResultDto dto = QuizAttemptResultDto.from(attempt);

        assertEquals(attemptId, dto.getAttemptId());
        assertEquals(new BigDecimal("85.0"), dto.getUserScore());
        assertEquals(new BigDecimal("100.0"), dto.getTotalScore());
        assertTrue(dto.isPassed());
    }
}

