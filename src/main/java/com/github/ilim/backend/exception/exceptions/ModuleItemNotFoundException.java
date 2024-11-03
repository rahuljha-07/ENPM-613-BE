package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class ModuleItemNotFoundException extends RuntimeException {

    public ModuleItemNotFoundException(UUID itemId) {
        super("CourseModuleItem(%s) is not found".formatted(itemId));
    }
}
