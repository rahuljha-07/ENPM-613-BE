package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyAccountDto {
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "confirmationCode cannot be null")
    private String confirmationCode;
}
