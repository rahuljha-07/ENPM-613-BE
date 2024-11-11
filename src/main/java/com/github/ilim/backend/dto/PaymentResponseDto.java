package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String stripeUrl;
}
