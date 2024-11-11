package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private String userId;
    private String courseId;
    private String courseName;
    private String courseDescription;
    private Double coursePrice;
    private String currency;
}
