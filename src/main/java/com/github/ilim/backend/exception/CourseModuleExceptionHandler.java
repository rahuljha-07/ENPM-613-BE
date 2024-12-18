package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.CourseModuleNotFoundException;
import com.github.ilim.backend.exception.exceptions.NotCourseInstructorException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CourseModuleExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(CourseModuleNotFoundException.class)
    public ApiRes<Res<String>> handleCourseModuleNotFoundException(CourseModuleNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(NotCourseInstructorException.class)
    public ApiRes<Res<String>> handleNotCourseInstructorException(NotCourseInstructorException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

}
