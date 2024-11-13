package com.github.ilim.backend.exception.exceptions;

import java.util.UUID;

public class PendingPurchaseExistsException extends RuntimeException {

    public PendingPurchaseExistsException(String studentId, UUID courseId, UUID purchaseId) {
        super("Student(%s) already has a pending CoursePurchase(%s) record for Course(%s)!"
            .formatted(studentId, purchaseId, courseId));
    }

}
