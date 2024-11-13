package com.github.ilim.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEventDto {
    private String userId;
    private String courseId;
    private String paymentId;
    private String paymentDate;
    private String status;
}
