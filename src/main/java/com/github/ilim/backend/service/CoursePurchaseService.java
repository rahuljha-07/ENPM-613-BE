package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoursePurchaseService {

    private final CoursePurchaseRepo purchaseRepo;

    public List<CoursePurchase> findAllByStudent(@NonNull User student) {
        return purchaseRepo.findAllByStudent(student);
    }

    public Optional<CoursePurchase> findByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return purchaseRepo.findByStudentAndCourse(student, course);
    }

    public void save(@NonNull CoursePurchase coursePurchase) {
        purchaseRepo.save(coursePurchase);
    }
}
