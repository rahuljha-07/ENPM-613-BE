package com.github.ilim.backend.util.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record Res<T>(
    LocalDateTime timestamp,
    HttpStatus status,
    T body
) {
    public static <E> Res<E> of(HttpStatus status, E body) {
        return new Res<E>(LocalDateTime.now(), status, body);
    }
}