package com.github.ilim.backend.repo;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface CoursePurchaseRepo extends JpaRepository<CoursePurchase, UUID> {

    List<CoursePurchase> findByStudentAndCourse(User student, Course course);

    List<CoursePurchase> findAllByStudent(User student);

    CoursePurchase findByStudentAndCourseAndStatus(User student, Course course, PurchaseStatus status);
}
