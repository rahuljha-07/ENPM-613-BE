package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModuleItemTypeTest {

    @Test
    void testEnumConstants() {
        assertNotNull(ModuleItemType.VIDEO, "Enum constant VIDEO should exist");
        assertNotNull(ModuleItemType.QUIZ, "Enum constant QUIZ should exist");
    }
}
