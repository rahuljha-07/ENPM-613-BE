package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PriorityLevelTest {

    @Test
    void testEnumConstants() {
        assertNotNull(PriorityLevel.LOW, "Enum constant LOW should exist");
        assertNotNull(PriorityLevel.MEDIUM, "Enum constant MEDIUM should exist");
        assertNotNull(PriorityLevel.HIGH, "Enum constant HIGH should exist");
    }
}
