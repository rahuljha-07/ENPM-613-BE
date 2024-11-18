package com.github.ilim.backend.util.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * Wrapper class for API responses, extending {@link ResponseEntity}.
 * <p>
 * Encapsulates the standardized response structure within {@link Res}.
 * </p>
 *
 * @param <T> the type of the response body
 */
public class ApiRes<T> extends ResponseEntity<T> {

    /**
     * Constructs a new {@link ApiRes} with the given body and status code.
     *
     * @param body   the body of the response
     * @param status the HTTP status code
     */
    public ApiRes(T body, HttpStatusCode status) {
        super(body, status);
    }
}
