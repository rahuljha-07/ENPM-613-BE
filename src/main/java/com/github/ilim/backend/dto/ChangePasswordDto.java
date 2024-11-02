package com.github.ilim.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @NotNull(message = "oldPassword cannot be null")
    private String oldPassword;
    @NotNull(message = "newPassword cannot be null")
    private String newPassword;
}
