package com.github.ilim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EmailDto {
    private String toAddress;
    private List<String> ccAddresses;
    private String subject;
    private String content;

    public EmailDto() {}
}


