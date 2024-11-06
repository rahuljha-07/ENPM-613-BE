package com.github.ilim.backend.dto;

import lombok.Data;

@Data
public class UpdateUserDto {

    private String profileImageUrl;

    private String title;

    private String bio;

}
