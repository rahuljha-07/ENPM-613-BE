package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class SignInDto {
    private String email;
    private String password;
}
