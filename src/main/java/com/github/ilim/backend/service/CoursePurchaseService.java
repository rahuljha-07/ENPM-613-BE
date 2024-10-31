package com.github.ilim.backend.service;

import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
import com.github.ilim.backend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoursePurchaseService {

    private final UserRepo userRepo;
    private final CoursePurchaseRepo purchaseRepo;

    public Optional<CoursePurchase> findByUserAndCourseId(User user, UUID courseId) {
        return purchaseRepo.findByStudentAndCourse_Id(user, courseId);
    }

}
