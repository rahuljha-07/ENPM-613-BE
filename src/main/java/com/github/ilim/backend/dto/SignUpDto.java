package com.github.ilim.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.exception.exceptions.MissingBirthdateException;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class SignUpDto {
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "password cannot be null")
    private String password;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "birthdate cannot be null")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthdate;

    public String getBirthdateString() {
        if (birthdate == null) {
            throw new MissingBirthdateException();
        }
        return birthdate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}