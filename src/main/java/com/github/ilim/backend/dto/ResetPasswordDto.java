package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String confirmationCode;
    private String newPassword;
}