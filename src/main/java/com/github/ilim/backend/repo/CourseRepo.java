package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepo extends JpaRepository<Course, UUID> {

    List<Course> findAllByStatus(CourseStatus status);

    List<Course> findAllByIdIn(List<UUID> ids);

    List<Course> findAllByInstructor(User instructor);
}
