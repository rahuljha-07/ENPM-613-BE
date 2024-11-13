package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.dto.PaymentRequestDto;
import com.github.ilim.backend.entity.Course;
import com.github.ilim.backend.entity.CoursePurchase;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.PaymentIntentStatus;
import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.CantFindCoursePurchaseException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.NoPurchasesException;
import com.github.ilim.backend.exception.exceptions.PendingPurchaseExistsException;
import com.github.ilim.backend.repo.CoursePurchaseRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoursePurchaseService {

    private final UserService userService;
    private final CourseService courseService;
    private final CoursePurchaseRepo purchaseRepo;
    private final PaymentService paymentService;

    private static final String DEFAULT_CURRENCY = "USD";

    public List<CoursePurchase> findAllByStudent(@NonNull User student) {
        return purchaseRepo.findAllByStudent(student);
    }

    public List<CoursePurchase> findByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return purchaseRepo.findByStudentAndCourse(student, course);
    }

    public CoursePurchase findPendingByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return findPendingByStudentAndCourse(student, course, PurchaseStatus.PENDING);
    }

    public CoursePurchase findPendingByStudentAndCourse(@NonNull User student, @NonNull Course course, PurchaseStatus status) {
        return purchaseRepo.findByStudentAndCourseAndStatus(student, course, status)
            .orElseThrow(() -> new CantFindCoursePurchaseException(student.getId(), course.getId(), status));
    }

    @Transactional
    public String purchaseCourse(User student, UUID courseId) {
        var course = courseService.findPublishedCourse(courseId);
        assertStudentNotCreatorOfCourse(student, course);
        assertCourseNotAlreadyPurchased(student, course);
        assertNoPendingPurchaseRequest(student, course);

        var purchase = CoursePurchase.createPendingPurchase(student, course);
        purchaseRepo.save(purchase);

        var paymentRequestDto = PaymentRequestDto.createRequestDto(student, course, DEFAULT_CURRENCY);
        return paymentService.createCheckoutSession(paymentRequestDto);
    }

    private void assertNoPendingPurchaseRequest(User student, Course course) {
        var purchase = purchaseRepo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.PENDING);
        if (purchase.isPresent()) {
            throw new PendingPurchaseExistsException(student.getId(), course.getId(), purchase.get().geCourseId());
        }
    }

    private void assertCourseNotAlreadyPurchased(User student, Course course) {
        if (didStudentPurchaseCourse(purchaseRepo, student, course)) {
            throw new AlreadyPurchasedCourseException(student.getId(), course.getId());
        }
    }

    private static void assertStudentNotCreatorOfCourse(User student, Course course) {
        if (course.getInstructor().getId().equals(student.getId())) {
            throw new CantPurchaseOwnCourseException(student.getId(), course.getId());
        }
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
        if (didStudentPurchaseCourse(purchaseRepo, student, course)) {
            return PurchaseStatus.SUCCEEDED;
        }
        var purchases = purchaseRepo.findByStudentAndCourse(student, course);
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.PENDING)) {
            return PurchaseStatus.PENDING;
        }
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.FAILED)) {
            return PurchaseStatus.FAILED;
        }
        throw new NoPurchasesException(student.getId(), courseId);
    }

    public static boolean didStudentPurchaseCourse(CoursePurchaseRepo repo, User student, Course course) {
        return repo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.SUCCEEDED).isPresent();
    }

    public void cancelCoursePurchase(User student, UUID courseId) {
        var course = courseService.findPublishedCourse(courseId);
        var purchase = findPendingByStudentAndCourse(student, course);
        purchase.setStatus(PurchaseStatus.FAILED);
        purchase.setFailedMessage("Course purchased cancelled by the student");
        purchaseRepo.save(purchase);
    }

    // This written mainly for the integration tests
    // however it can be used by the admin to gift a course to a student for free
    @Transactional
    public void simulateCoursePurchaseConfirmation(PaymentEventDto dto) {
        confirmPurchase(dto);
    }
}
