package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepo extends JpaRepository<Course, UUID> {

    List<Course> findAllByStatusAndIsDeleted(CourseStatus status, boolean deleted);

    List<Course> findAllByIdInAndIsDeleted(List<UUID> ids, boolean deleted);

    List<Course> findAllByInstructorAndIsDeleted(User instructor, boolean deleted);
}
