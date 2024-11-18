package com.github.ilim.backend.util.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Generic response wrapper for API responses.
 * <p>
 * Encapsulates the response timestamp, HTTP status, and the response body.
 * </p>
 *
 * @param <T> the type of the response body
 */
public record Res<T>(
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp,
    HttpStatus status,
    T body
) {
    /**
     * Creates a new {@link Res} instance with the current timestamp, specified status, and body.
     *
     * @param status the {@link HttpStatus} of the response
     * @param body   the response body
     * @param <E>    the type of the response body
     * @return a new {@link Res} instance
     */
    public static <E> Res<E> of(HttpStatus status, E body) {
        return new Res<E>(LocalDateTime.now(), status, body);
    }
}
