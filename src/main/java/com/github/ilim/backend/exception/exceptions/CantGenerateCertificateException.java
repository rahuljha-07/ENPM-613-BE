package com.github.ilim.backend.exception.exceptions;

import com.github.ilim.backend.entity.Course;

public class CantGenerateCertificateException extends RuntimeException {

    public CantGenerateCertificateException(Course course, Exception e) {
        super("Failed to generate a certificate for Course(%s)".formatted(course.getId()), e);
    }

}
