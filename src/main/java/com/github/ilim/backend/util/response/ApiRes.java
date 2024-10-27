package com.github.ilim.backend.util.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ApiRes<T> extends ResponseEntity<T> {

    public ApiRes(T body, HttpStatusCode status) {
        super(body, status);
    }

}
