package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CourseExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(UserCannotCreateCourseException.class)
    public ApiRes<Res<String>> handleUserCannotCreateCourseException(UserCannotCreateCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(UserHasNoAccessToCourseException.class)
    public ApiRes<Res<String>> handleUserHasNoAccessToCourseException(UserHasNoAccessToCourseException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ApiRes<Res<String>> handleCourseNotFoundException(CourseNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

}
