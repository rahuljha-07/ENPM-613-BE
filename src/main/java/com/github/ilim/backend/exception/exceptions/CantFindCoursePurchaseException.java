package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.enums.PurchaseStatus;

import java.util.UUID;

public class CantFindCoursePurchaseException extends RuntimeException {

    public CantFindCoursePurchaseException(String userId, UUID courseId, PurchaseStatus status) {
        super("Can not find a %s CoursePurchase for User(%s) and Course(%s)".formatted(status, userId, courseId));
    }
}
