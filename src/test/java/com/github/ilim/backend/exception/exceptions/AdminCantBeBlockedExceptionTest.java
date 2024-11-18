package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminCantBeBlockedExceptionTest {

    @Test
    void testConstructorWithUserId() {
        String userId = "admin-789";
        AdminCantBeBlockedException exception = new AdminCantBeBlockedException(userId);
        assertEquals("User() has role ADMIN, and admins can not be blocked.".replace("()", "(" + userId + ")"), exception.getMessage());
    }
}
