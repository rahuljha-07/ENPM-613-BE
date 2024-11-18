package com.github.ilim.backend.service;

import com.github.ilim.backend.dto.PaymentEventDto;
import com.github.ilim.backend.dto.PaymentRequestDto;
import com.github.ilim.backend.entity.AuditEntity;
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

/**
 * Service class responsible for managing course purchases.
 * <p>
 * Provides functionalities such as initiating a purchase, confirming or rejecting purchases,
 * checking purchase status, and canceling purchases. Interacts with {@link UserService},
 * {@link CourseService}, {@link CoursePurchaseRepo}, and {@link PaymentService}.
 * </p>
 *
 * @see UserService
 * @see CourseService
 * @see CoursePurchaseRepo
 * @see PaymentService
 */
@Service
@RequiredArgsConstructor
public class CoursePurchaseService {

    private final UserService userService;
    private final CourseService courseService;
    private final CoursePurchaseRepo purchaseRepo;
    private final PaymentService paymentService;

    private static final String DEFAULT_CURRENCY = "USD";

    /**
     * Retrieves all course purchases made by a specific student.
     *
     * @param student the {@link User} entity representing the student
     * @return a list of {@link CoursePurchase} entities representing the student's purchases
     */
    public List<CoursePurchase> findAllByStudent(@NonNull User student) {
        return purchaseRepo.findAllByStudent(student, AuditEntity.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Retrieves all purchases of a specific course by a student.
     *
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @return a list of {@link CoursePurchase} entities for the specified course
     */
    public List<CoursePurchase> findByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return purchaseRepo.findByStudentAndCourse(student, course, AuditEntity.SORT_BY_CREATED_AT_DESC);
    }

    /**
     * Finds a pending course purchase for a student and course.
     *
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @return the {@link CoursePurchase} entity with {@link PurchaseStatus#PENDING}
     * @throws CantFindCoursePurchaseException if no pending purchase is found
     */
    public CoursePurchase findPendingByStudentAndCourse(@NonNull User student, @NonNull Course course) {
        return findPendingByStudentAndCourse(student, course, PurchaseStatus.PENDING);
    }

    /**
     * Finds a course purchase with a specific status for a student and course.
     *
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @param status  the {@link PurchaseStatus} to filter by
     * @return the {@link CoursePurchase} entity matching the criteria
     * @throws CantFindCoursePurchaseException if no matching purchase is found
     */
    public CoursePurchase findPendingByStudentAndCourse(@NonNull User student, @NonNull Course course, PurchaseStatus status) {
        return purchaseRepo.findByStudentAndCourseAndStatus(student, course, status)
            .orElseThrow(() -> new CantFindCoursePurchaseException(student.getId(), course.getId(), status));
    }

    /**
     * Initiates the purchase process for a course.
     * <p>
     * Validates that the student is not the creator of the course, has not already purchased the course,
     * and does not have any pending purchase requests. Creates a pending purchase and initiates the payment process.
     * </p>
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course to purchase
     * @return a checkout URL string for the payment process
     * @throws CantPurchaseOwnCourseException   if the student is the creator of the course
     * @throws AlreadyPurchasedCourseException if the student has already purchased the course
     * @throws PendingPurchaseExistsException  if there is an existing pending purchase request
     */
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

    /**
     * Asserts that there is no pending purchase request for the specified student and course.
     *
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @throws PendingPurchaseExistsException if a pending purchase request exists
     */
    private void assertNoPendingPurchaseRequest(User student, Course course) {
        var purchase = purchaseRepo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.PENDING);
        if (purchase.isPresent()) {
            throw new PendingPurchaseExistsException(student.getId(), course.getId(), purchase.get().geCourseId());
        }
    }

    /**
     * Asserts that the student has not already purchased the specified course.
     *
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @throws AlreadyPurchasedCourseException if the course has already been purchased by the student
     */
    private void assertCourseNotAlreadyPurchased(User student, Course course) {
        if (didStudentPurchaseCourse(purchaseRepo, student, course)) {
            throw new AlreadyPurchasedCourseException(student.getId(), course.getId());
        }
    }

    /**
     * Asserts that the student is not the creator of the course.
     *
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @throws CantPurchaseOwnCourseException if the student is the instructor of the course
     */
    private static void assertStudentNotCreatorOfCourse(User student, Course course) {
        if (course.getInstructor().getId().equals(student.getId())) {
            throw new CantPurchaseOwnCourseException(student.getId(), course.getId());
        }
    }

    /**
     * Confirms a successful course purchase.
     * <p>
     * Updates the purchase status to {@link PurchaseStatus#SUCCEEDED} and records the payment ID.
     * </p>
     *
     * @param paymentEventDto the {@link PaymentEventDto} containing payment details
     * @throws CantFindCoursePurchaseException if no pending purchase is found for the student and course
     */
    @Transactional
    public void confirmPurchase(@NonNull PaymentEventDto paymentEventDto) {
        var student = userService.findById(paymentEventDto.getUserId());
        var course = courseService.findPublishedCourse(UUID.fromString(paymentEventDto.getCourseId()));

        var purchase = findPendingByStudentAndCourse(student, course);
        purchase.setPaymentId(paymentEventDto.getPaymentId());
        purchase.setStatus(PurchaseStatus.SUCCEEDED);
        purchaseRepo.save(purchase);
    }

    /**
     * Rejects a course purchase due to payment issues.
     * <p>
     * Updates the purchase status to {@link PurchaseStatus#FAILED} and records the failure reason.
     * </p>
     *
     * @param paymentEventDto the {@link PaymentEventDto} containing payment details
     * @param status          the {@link PaymentIntentStatus} indicating the reason for rejection
     * @throws CantFindCoursePurchaseException if no pending purchase is found for the student and course
     */
    public void rejectPurchase(PaymentEventDto paymentEventDto, PaymentIntentStatus status) {
        var student = userService.findById(paymentEventDto.getUserId());
        var course = courseService.findPublishedCourse(UUID.fromString(paymentEventDto.getCourseId()));

        var purchase = findPendingByStudentAndCourse(student, course);
        purchase.setPaymentId(paymentEventDto.getPaymentId());
        purchase.setStatus(PurchaseStatus.FAILED);
        purchase.setFailedMessage("Course purchase failed due to %s".formatted(status));
        purchaseRepo.save(purchase);
    }

    /**
     * Checks the purchase status of a course for a student.
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course
     * @return the {@link PurchaseStatus} indicating the purchase state
     * @throws NoPurchasesException if there are no purchases found for the student and course
     */
    public PurchaseStatus checkCoursePurchase(User student, UUID courseId) {
        var course = courseService.findPublishedCourse(courseId);
        if (didStudentPurchaseCourse(purchaseRepo, student, course)) {
            return PurchaseStatus.SUCCEEDED;
        }
        var purchases = purchaseRepo.findByStudentAndCourse(student, course, AuditEntity.SORT_BY_CREATED_AT_DESC);
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.PENDING)) {
            return PurchaseStatus.PENDING;
        }
        if (purchases.stream().anyMatch(purchase -> purchase.getStatus() == PurchaseStatus.FAILED)) {
            return PurchaseStatus.FAILED;
        }
        throw new NoPurchasesException(student.getId(), courseId);
    }

    /**
     * Determines whether a student has successfully purchased a course.
     *
     * @param repo    the {@link CoursePurchaseRepo} repository
     * @param student the {@link User} entity representing the student
     * @param course  the {@link Course} entity representing the course
     * @return {@code true} if the student has purchased the course, {@code false} otherwise
     */
    public static boolean didStudentPurchaseCourse(CoursePurchaseRepo repo, User student, Course course) {
        return repo.findByStudentAndCourseAndStatus(student, course, PurchaseStatus.SUCCEEDED).isPresent();
    }

    /**
     * Cancels a pending course purchase.
     * <p>
     * Updates the purchase status to {@link PurchaseStatus#FAILED} and records the cancellation reason.
     * </p>
     *
     * @param student  the {@link User} entity representing the student
     * @param courseId the unique identifier of the course
     * @throws CantFindCoursePurchaseException if no pending purchase is found for the student and course
     */
    public void cancelCoursePurchase(User student, UUID courseId) {
        var course = courseService.findPublishedCourse(courseId);
        var purchase = findPendingByStudentAndCourse(student, course);
        purchase.setStatus(PurchaseStatus.FAILED);
        purchase.setFailedMessage("Course purchased cancelled by the student");
        purchaseRepo.save(purchase);
    }

    /**
     * Simulates the confirmation of a course purchase.
     * <p>
     * Primarily used for integration testing or admin gifting courses to students.
     * </p>
     *
     * @param dto the {@link PaymentEventDto} containing payment details
     */
    @Transactional
    public void simulateCoursePurchaseConfirmation(PaymentEventDto dto) {
        confirmPurchase(dto);
    }
}
