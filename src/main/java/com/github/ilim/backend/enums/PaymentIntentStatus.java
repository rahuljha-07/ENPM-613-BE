package com.github.ilim.backend.enums;

public enum PaymentIntentStatus {
    SUCCEEDED,
    REQUIRES_ACTION,
    REQUIRES_PAYMENT_METHOD,
    PROCESSING,
    CANCELED,
    REQUIRES_CONFIRMATION,
    REQUIRES_CAPTURE,
    NULL;

    public static PaymentIntentStatus from(String status) {
        return switch (status) {
            case "succeeded" -> SUCCEEDED;
            case "requires_action" -> REQUIRES_ACTION;
            case "requires_payment_method" -> REQUIRES_PAYMENT_METHOD;
            case "processing" -> PROCESSING;
            case "canceled" -> CANCELED;
            case "requires_confirmation" -> REQUIRES_CONFIRMATION;
            case "requires_capture" -> REQUIRES_CAPTURE;
            default -> NULL;
        };
    }

    public String to(PaymentIntentStatus status) {
        return switch (status) {
            case SUCCEEDED -> "succeeded";
            case REQUIRES_ACTION -> "requires_action";
            case REQUIRES_PAYMENT_METHOD -> "requires_payment_method";
            case PROCESSING -> "processing";
            case CANCELED -> "canceled";
            case REQUIRES_CONFIRMATION -> "requires_confirmation";
            case REQUIRES_CAPTURE -> "requires_capture";
            default -> null;
        };
    }
}