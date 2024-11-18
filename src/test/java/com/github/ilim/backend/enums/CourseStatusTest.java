package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CourseStatusTest {

    @Test
    void testEnumConstants() {
        assertNotNull(CourseStatus.DRAFT, "Enum constant DRAFT should exist");
        assertNotNull(CourseStatus.WAIT_APPROVAL, "Enum constant WAIT_APPROVAL should exist");
        assertNotNull(CourseStatus.PUBLISHED, "Enum constant PUBLISHED should exist");
    }
}
