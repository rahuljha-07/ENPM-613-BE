package com.github.ilim.backend.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAnswerOptionTest {

    @Test
    void testFrom() {
        UserAnswer answer = new UserAnswer();
        answer.setId(UUID.randomUUID());

        QuestionOption option = new QuestionOption();
        option.setId(UUID.randomUUID());

        UserAnswerOption answerOption = UserAnswerOption.from(answer, option);

        assertEquals(answer, answerOption.getUserAnswer());
        assertEquals(option, answerOption.getSelectedOption());
    }
}
