package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.exception.exceptions.ModuleItemNotFoundException;
import com.github.ilim.backend.repo.ModuleItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModuleItemService {

    private final ModuleItemRepo moduleItemRepo;

    public CourseModuleItem findModuleItemByVideo(Video video) {
        return moduleItemRepo.findByVideo(video)
            .orElseThrow(() -> new ModuleItemNotFoundException(video));
    }

    public CourseModuleItem findModuleItemByQuiz(Quiz quiz) {
        return moduleItemRepo.findByQuiz(quiz)
            .orElseThrow(() -> new ModuleItemNotFoundException(quiz));
    }

}
