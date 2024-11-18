package com.github.ilim.backend.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRoleTest {

    @Test
    void testEnumConstants() {
        assertNotNull(UserRole.STUDENT, "Enum constant STUDENT should exist");
        assertNotNull(UserRole.INSTRUCTOR, "Enum constant INSTRUCTOR should exist");
        assertNotNull(UserRole.ADMIN, "Enum constant ADMIN should exist");
    }
}
