package com.github.ilim.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmailDto {
    private String toAddress;
    private List<String> ccAddresses;
    private String subject;
    private String content;
}


