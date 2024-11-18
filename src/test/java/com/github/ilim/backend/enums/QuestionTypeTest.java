package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionTypeTest {

    @Test
    void testEnumConstants() {
        assertNotNull(QuestionType.TRUE_FALSE, "Enum constant TRUE_FALSE should exist");
        assertNotNull(QuestionType.MULTIPLE_CHOICE, "Enum constant MULTIPLE_CHOICE should exist");
    }
}
