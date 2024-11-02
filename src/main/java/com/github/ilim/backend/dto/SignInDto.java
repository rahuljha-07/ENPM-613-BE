package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignInDto {
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "password cannot be null")
    private String password;
}
