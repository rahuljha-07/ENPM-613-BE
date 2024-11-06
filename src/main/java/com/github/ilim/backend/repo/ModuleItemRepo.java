package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.CourseModule;
import com.github.ilim.backend.entity.CourseModuleItem;
import com.github.ilim.backend.entity.Quiz;
import com.github.ilim.backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ModuleItemRepo extends JpaRepository<CourseModuleItem, UUID> {

    Optional<CourseModuleItem> findByVideo(Video video);

    Optional<CourseModuleItem> findByQuiz(Quiz quiz);

}
