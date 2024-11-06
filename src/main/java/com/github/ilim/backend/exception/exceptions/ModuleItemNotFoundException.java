package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;

import java.util.UUID;

public class ModuleItemNotFoundException extends RuntimeException {

    public ModuleItemNotFoundException(UUID itemId) {
        super("CourseModuleItem(%s) is not found".formatted(itemId));
    }

    public ModuleItemNotFoundException(Video video) {
        super("No CourseModuleItem found for Video(%s)".formatted(video.getId()));
    }

    public ModuleItemNotFoundException(Quiz quiz) {
        super("No CourseModuleItem found for Quiz(%s)".formatted(quiz.getId()));
    }

}
