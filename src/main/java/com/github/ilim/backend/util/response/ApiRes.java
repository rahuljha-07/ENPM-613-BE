package com.github.ilim.backend.util.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class ApiRes<T> extends ResponseEntity<T> {
    public ApiRes(HttpStatusCode status) {
        super(status);
    }

    public ApiRes(T body, HttpStatusCode status) {
        super(body, status);
    }

    public ApiRes(MultiValueMap<String, String> headers, HttpStatusCode status) {
        super(headers, status);
    }

    public ApiRes(T body, MultiValueMap<String, String> headers, int rawStatus) {
        super(body, headers, rawStatus);
    }

    public ApiRes(T body, MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
        super(body, headers, statusCode);
    }

}
