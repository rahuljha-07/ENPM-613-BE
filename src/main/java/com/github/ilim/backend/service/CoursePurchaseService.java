package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.dto.PaymentRequestDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PaymentIntentStatus;
import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.NoPurchasesException;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoursePurchaseService {

    private final UserService userService;
    private final CourseService courseService;
    private final CoursePurchaseRepo purchaseRepo;
    private final PaymentService paymentService;

    public List<CoursePurchase> findAllByStudent(@NonNull User student) {
        return purchaseRepo.findAllByStudent(student);
    }

    public List<CoursePurchase> findByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return purchaseRepo.findByStudentAndCourse(student, course);
    }

    public CoursePurchase findPendingByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return purchaseRepo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.PENDING);
    }

    @Transactional
    public void save(@NonNull CoursePurchase coursePurchase) {
        purchaseRepo.save(coursePurchase);
    }

    @Transactional
    public String purchaseCourse(User student, UUID courseId) {
        var course = courseService.findPublishedCourse(courseId);
        var succeededPurchase = purchaseRepo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.SUCCEEDED);

        if (succeededPurchase != null) {
            throw new AlreadyPurchasedCourseException(student.getId(), courseId);
        }

        if (course.getInstructor().getId().equals(student.getId())) {
            throw new CantPurchaseOwnCourseException(student.getId(), course.getId());
        }

        var purchase = new CoursePurchase();
        purchase.setStudent(student);
        purchase.setCourse(course);
        purchase.setPurchasePrice(course.getPrice());
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setStatus(PurchaseStatus.PENDING);
        purchaseRepo.save(purchase);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setCourseId(course.getId().toString());
        paymentRequestDto.setCourseName(course.getTitle());
        paymentRequestDto.setCourseDescription(course.getDescription());
        paymentRequestDto.setCoursePrice(course.getPrice().doubleValue());
        paymentRequestDto.setCurrency("USD");
        paymentRequestDto.setUserId(student.getId());

        return paymentService.createCheckoutSession(paymentRequestDto);
    }

    @Transactional
    public void confirmPurchase(@NonNull PaymentEventDto paymentEventDto) {
        var student = userService.findById(paymentEventDto.getUserId());
        var course = courseService.findPublishedCourse(UUID.fromString(paymentEventDto.getCourseId()));

        var purchase = findPendingByStudentAndCourse(student, course);
        purchase.setPaymentId(paymentEventDto.getPaymentId());
        purchase.setStatus(PurchaseStatus.SUCCEEDED);
        purchaseRepo.save(purchase);
    }

    public void rejectPurchase(PaymentEventDto paymentEventDto, PaymentIntentStatus status) {
        var student = userService.findById(paymentEventDto.getUserId());
        var course = courseService.findPublishedCourse(UUID.fromString(paymentEventDto.getCourseId()));

        var purchase = findPendingByStudentAndCourse(student, course);
        purchase.setPaymentId(paymentEventDto.getPaymentId());
        purchase.setStatus(PurchaseStatus.FAILED);
        purchase.setFailedMessage("Course purchase failed due to %s".formatted(status));
        purchaseRepo.save(purchase);
    }

    public PurchaseStatus checkCoursePurchase(User student, UUID courseId) {
        var course = courseService.findPublishedCourse(courseId);
        var purchases = purchaseRepo.findByStudentAndCourse(student, course);
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.SUCCEEDED)) {
            return PurchaseStatus.SUCCEEDED;
        }
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.PENDING)) {
            return PurchaseStatus.PENDING;
        }
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.FAILED)) {
            return PurchaseStatus.FAILED;
        }
        throw new NoPurchasesException(student.getId(), courseId);
    }
}
