package com.github.ilim.backend.util.response;

import org.springframework.http.HttpStatus;

/**
 * Utility class for creating standardized API responses.
 * <p>
 * Provides static methods to create {@link ApiRes} instances for various HTTP statuses.
 * </p>
 */
public class Reply {

    /**
     * Creates a response with the specified HTTP status and no body.
     *
     * @param status the {@link HttpStatus} of the response
     * @param <T>    the type of the response body
     * @return a new {@link ApiRes} instance with the specified status and no body
     */
    public static <T> ApiRes<Res<T>> create(HttpStatus status) {
        return create(status, null);
    }

    /**
     * Creates a response with the specified HTTP status and body.
     *
     * @param status  the {@link HttpStatus} of the response
     * @param message the response body
     * @param <T>     the type of the response body
     * @return a new {@link ApiRes} instance with the specified status and body
     */
    public static <T> ApiRes<Res<T>> create(HttpStatus status, T message) {
        return new ApiRes<Res<T>>(Res.of(status, message), status);
    }

    /**
     * Creates a successful {@link ApiRes} with HTTP status 200 (OK) and no body.
     *
     * @return a new {@link ApiRes} instance with status 200 and no body
     */
    public static ApiRes<Res<String>> ok() {
        return ok(null);
    }

    /**
     * Creates a successful {@link ApiRes} with HTTP status 200 (OK) and the specified body.
     *
     * @param body the response body
     * @param <T>  the type of the response body
     * @return a new {@link ApiRes} instance with status 200 and the specified body
     */
    public static <T> ApiRes<Res<T>> ok(T body) {
        return create(HttpStatus.OK, body);
    }

    /**
     * Creates a created {@link ApiRes} with HTTP status 201 (Created) and no body.
     *
     * @return a new {@link ApiRes} instance with status 201 and no body
     */
    public static <T> ApiRes<Res<T>> created() {
        return created(null);
    }

    /**
     * Creates a created {@link ApiRes} with HTTP status 201 (Created) and the specified body.
     *
     * @param body the response body
     * @param <T>  the type of the response body
     * @return a new {@link ApiRes} instance with status 201 and the specified body
     */
    public static <T> ApiRes<Res<T>> created(T body) {
        return create(HttpStatus.CREATED, body);
    }

    /**
     * Creates a bad request {@link ApiRes} with HTTP status 400 (Bad Request) and no message.
     *
     * @return a new {@link ApiRes} instance with status 400 and no message
     */
    public static ApiRes<Res<String>> badRequest() {
        return badRequest(null);
    }

    /**
     * Creates a bad request {@link ApiRes} with HTTP status 400 (Bad Request) and the specified message.
     *
     * @param message the error message
     * @return a new {@link ApiRes} instance with status 400 and the specified message
     */
    public static ApiRes<Res<String>> badRequest(String message) {
        return create(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Creates a conflict {@link ApiRes} with HTTP status 409 (Conflict) and no message.
     *
     * @return a new {@link ApiRes} instance with status 409 and no message
     */
    public static ApiRes<Res<String>> conflict() {
        return conflict(null);
    }

    /**
     * Creates a conflict {@link ApiRes} with HTTP status 409 (Conflict) and the specified message.
     *
     * @param message the error message
     * @return a new {@link ApiRes} instance with status 409 and the specified message
     */
    public static ApiRes<Res<String>> conflict(String message) {
        return create(HttpStatus.CONFLICT, message);
    }

    /**
     * Creates a not found {@link ApiRes} with HTTP status 404 (Not Found) and no message.
     *
     * @return a new {@link ApiRes} instance with status 404 and no message
     */
    public static ApiRes<Res<String>> notFound() {
        return notFound(null);
    }

    /**
     * Creates a not found {@link ApiRes} with HTTP status 404 (Not Found) and the specified message.
     *
     * @param message the error message
     * @return a new {@link ApiRes} instance with status 404 and the specified message
     */
    public static ApiRes<Res<String>> notFound(String message) {
        return create(HttpStatus.NOT_FOUND, message);
    }

    /**
     * Creates an unauthorized {@link ApiRes} with HTTP status 401 (Unauthorized) and no message.
     *
     * @return a new {@link ApiRes} instance with status 401 and no message
     */
    public static ApiRes<Res<String>> unauthorized() {
        return unauthorized(null);
    }

    /**
     * Creates an unauthorized {@link ApiRes} with HTTP status 401 (Unauthorized) and the specified message.
     *
     * @param message the error message
     * @return a new {@link ApiRes} instance with status 401 and the specified message
     */
    public static ApiRes<Res<String>> unauthorized(String message) {
        return create(HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * Creates a forbidden {@link ApiRes} with HTTP status 403 (Forbidden) and no message.
     *
     * @return a new {@link ApiRes} instance with status 403 and no message
     */
    public static ApiRes<Res<String>> forbidden() {
        return forbidden(null);
    }

    /**
     * Creates a forbidden {@link ApiRes} with HTTP status 403 (Forbidden) and the specified message.
     *
     * @param message the error message
     * @return a new {@link ApiRes} instance with status 403 and the specified message
     */
    public static ApiRes<Res<String>> forbidden(String message) {
        return create(HttpStatus.FORBIDDEN, message);
    }
}
