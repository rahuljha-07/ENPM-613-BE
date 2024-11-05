package com.github.ilim.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmailDto {
    private String toAddress;
    private List<String> ccAddresses;
    private String subject;
    private String content;
}


