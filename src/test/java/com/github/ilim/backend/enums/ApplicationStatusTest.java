package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationStatusTest {

    @Test
    void testEnumConstants() {
        assertNotNull(ApplicationStatus.REJECTED, "Enum constant REJECTED should exist");
        assertNotNull(ApplicationStatus.PENDING, "Enum constant PENDING should exist");
        assertNotNull(ApplicationStatus.APPROVED, "Enum constant APPROVED should exist");
    }
}
