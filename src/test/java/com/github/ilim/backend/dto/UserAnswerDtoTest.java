package com.github.ilim.backend.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserAnswerDtoTest {

    @Test
    void testUserAnswerDtoFields() {
        UserAnswerDto dto = new UserAnswerDto();
        UUID questionId = UUID.randomUUID();
        UUID optionId1 = UUID.randomUUID();
        UUID optionId2 = UUID.randomUUID();

        dto.setQuestionId(questionId);
        dto.setSelectedOptionIds(Arrays.asList(optionId1, optionId2));

        assertEquals(questionId, dto.getQuestionId());
        assertEquals(Arrays.asList(optionId1, optionId2), dto.getSelectedOptionIds());
    }
}
