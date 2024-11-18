package com.github.ilim.backend.exception.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnlyAdminAccessAllCoursesTest {

    @Test
    void testConstructorWithAdminId() {
        String adminId = "admin-1212";
        OnlyAdminAccessAllCourses exception = new OnlyAdminAccessAllCourses(adminId);
        assertEquals("User() is not admin. Only admin can access all courses in the system".replace("()", "(" + adminId + ")"), exception.getMessage());
    }
}
