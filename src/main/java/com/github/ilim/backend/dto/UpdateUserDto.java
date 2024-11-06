package com.github.ilim.backend.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UpdateUserDto {

    private String profileImageUrl;

    private String title;

    private String bio;

}
