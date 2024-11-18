package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import com.github.ilim.backend.exception.exceptions.ModuleItemNotFoundException;
import com.github.ilim.backend.repo.ModuleItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing course module items.
 * <p>
 * Provides functionalities to retrieve module items based on associated videos or quizzes.
 * </p>
 *
 * @see ModuleItemRepo
 */
@Service
@RequiredArgsConstructor
public class ModuleItemService {

    private final ModuleItemRepo moduleItemRepo;

    /**
     * Finds a course module item associated with a given video.
     *
     * @param video the {@link Video} entity to find the corresponding module item for
     * @return the {@link CourseModuleItem} associated with the provided video
     * @throws ModuleItemNotFoundException if no module item is found for the given video
     */
    public CourseModuleItem findModuleItemByVideo(Video video) {
        return moduleItemRepo.findByVideo(video)
            .orElseThrow(() -> new ModuleItemNotFoundException(video));
    }

    /**
     * Finds a course module item associated with a given quiz.
     *
     * @param quiz the {@link Quiz} entity to find the corresponding module item for
     * @return the {@link CourseModuleItem} associated with the provided quiz
     * @throws ModuleItemNotFoundException if no module item is found for the given quiz
     */
    public CourseModuleItem findModuleItemByQuiz(Quiz quiz) {
        return moduleItemRepo.findByQuiz(quiz)
            .orElseThrow(() -> new ModuleItemNotFoundException(quiz));
    }
}
