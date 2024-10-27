package com.github.ilim.backend.util.response;

import org.springframework.http.HttpStatus;

public class Reply {

    public static <T> ApiRes<Res<T>> create(HttpStatus status) {
        return create(status, null);
    }

    public static <T> ApiRes<Res<T>> create(HttpStatus status, T message) {
        return new ApiRes(Res.of(status, message), status);
    }

    public static ApiRes<Res<String>> ok() {
        return ok(null);
    }

    public static <T> ApiRes<Res<T>> ok(T body) {
        return create(HttpStatus.OK, body);
    }

    public static ApiRes<Res<String>> badRequest() {
        return badRequest(null);
    }

    public static ApiRes<Res<String>> badRequest(String message) {
        return create(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiRes<Res<String>> conflict() {
        return conflict(null);
    }

    public static ApiRes<Res<String>> conflict(String message) {
        return create(HttpStatus.CONFLICT, message);
    }

    public static ApiRes<Res<String>> notFound() {
        return notFound(null);
    }

    public static ApiRes<Res<String>> notFound(String message) {
        return create(HttpStatus.NOT_FOUND, message);
    }

    public static ApiRes<Res<String>> unauthorized() {
        return unauthorized(null);
    }

    public static ApiRes<Res<String>> unauthorized(String message) {
        return create(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiRes<Res<String>> forbidden() {
        return forbidden(null);
    }

    public static ApiRes<Res<String>> forbidden(String message) {
        return create(HttpStatus.FORBIDDEN, message);
    }
}
