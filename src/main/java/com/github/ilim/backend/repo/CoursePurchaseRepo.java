package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface CoursePurchaseRepo extends JpaRepository<CoursePurchase, UUID> {

    Optional<CoursePurchase> findByStudentAndCourse(User student, Course course);
}
