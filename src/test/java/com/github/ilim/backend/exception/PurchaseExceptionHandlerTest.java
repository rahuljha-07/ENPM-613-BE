package com.github.ilim.backend.exception;

import com.github.ilim.backend.enums.PurchaseStatus;
import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.CantFindCoursePurchaseException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.NoPurchasesException;
import com.github.ilim.backend.exception.exceptions.PendingPurchaseExistsException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchaseExceptionHandlerTest {

    private PurchaseExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PurchaseExceptionHandler();
    }

    @Test
    void handleAlreadyPurchasedCourseException() {
        String studentId = "student-101";
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "Student(" + studentId + ") already purchased Course(" + courseId + ")!";
        AlreadyPurchasedCourseException exception = new AlreadyPurchasedCourseException(studentId, courseId);
        ApiRes<Res<String>> response = handler.handleAlreadyPurchasedCourseException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCantPurchaseOwnCourseException() {
        String studentId = "student-102";
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "User(" + studentId + ") cannot buy Course (" + courseId + ") because user is the instructor of the course!";
        CantPurchaseOwnCourseException exception = new CantPurchaseOwnCourseException(studentId, courseId);
        ApiRes<Res<String>> response = handler.handleCantPurchaseOwnCourseException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handlePendingPurchaseExistsException() {
        String studentId = "student-103";
        UUID courseId = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();
        String expectedMessage = "Student(" + studentId + ") already has a pending CoursePurchase(" + purchaseId + ") record for Course(" + courseId + ")!";
        PendingPurchaseExistsException exception = new PendingPurchaseExistsException(studentId, courseId, purchaseId);
        ApiRes<Res<String>> response = handler.handlePendingPurchaseExistsException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCantFindCoursePurchaseException() {
        String userId = "user-105";
        UUID courseId = UUID.randomUUID();
        PurchaseStatus status = PurchaseStatus.PENDING;
        String expectedMessage = "Can not find a " + status + " CoursePurchase for User(" + userId + ") and Course(" + courseId + ")";
        CantFindCoursePurchaseException exception = new CantFindCoursePurchaseException(userId, courseId, status);
        ApiRes<Res<String>> response = handler.handleCantFindCoursePurchaseException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleNoPurchasesException() {
        String studentId = "student-104";
        UUID courseId = UUID.randomUUID();
        String expectedMessage = "We couldn't find any purchase for the student(" + studentId + ") and course(" + courseId + ")";
        NoPurchasesException exception = new NoPurchasesException(studentId, courseId);
        ApiRes<Res<String>> response = handler.handleNoPurchasesException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }
}
