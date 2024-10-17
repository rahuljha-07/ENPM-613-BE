package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private Integer expiresIn;
}
