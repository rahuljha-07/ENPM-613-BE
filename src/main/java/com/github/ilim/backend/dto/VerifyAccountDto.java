package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class VerifyAccountDto {
    private String email;
    private String confirmationCode;
}
