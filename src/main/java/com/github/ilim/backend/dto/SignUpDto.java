package com.github.ilim.backend.dto;

import com.github.ilim.backend.exception.exceptions.MissingBirthdateException;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private LocalDate birthdate;

    public String getBirthdateString() {
        if (birthdate == null) {
            throw new MissingBirthdateException();
        }
        return birthdate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}