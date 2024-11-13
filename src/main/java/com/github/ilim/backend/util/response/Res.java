package com.github.ilim.backend.util.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record Res<T>(
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp,
    HttpStatus status,
    T body
) {
    public static <E> Res<E> of(HttpStatus status, E body) {
        return new Res<E>(LocalDateTime.now(), status, body);
    }
}