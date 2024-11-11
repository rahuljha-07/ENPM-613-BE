package com.github.ilim.backend.exception.exceptions;

public class OnlyAdminAccessAllCourses extends RuntimeException {
    public OnlyAdminAccessAllCourses(String adminId) {
        super("User(%s) is not admin. Only admin can access all courses in the system".formatted(adminId));
    }
}
