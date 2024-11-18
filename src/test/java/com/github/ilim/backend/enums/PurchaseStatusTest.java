package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PurchaseStatusTest {

    @Test
    void testEnumConstants() {
        assertNotNull(PurchaseStatus.SUCCEEDED, "Enum constant SUCCEEDED should exist");
        assertNotNull(PurchaseStatus.PENDING, "Enum constant PENDING should exist");
        assertNotNull(PurchaseStatus.FAILED, "Enum constant FAILED should exist");
    }
}
