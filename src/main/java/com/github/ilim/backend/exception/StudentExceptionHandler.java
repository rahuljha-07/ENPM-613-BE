package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
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
public class StudentExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(InstructorAppAlreadyExistsException.class)
    public ApiRes<Res<String>> handleInstructorAppAlreadyExistsException(InstructorAppAlreadyExistsException e) {
        logger.warning(e.getMessage());
        return Reply.conflict(e.getMessage());
    }

    @ExceptionHandler(InstructorAppNotFoundException.class)
    public ApiRes<Res<String>> handleInstructorAppNotFoundException(InstructorAppNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyInstructorException.class)
    public ApiRes<Res<String>> handleUserAlreadyInstructorException(UserAlreadyInstructorException e) {
        logger.warning(e.getMessage());
        return Reply.conflict(e.getMessage());
    }

    @ExceptionHandler(AdminCannotBeInstructorException.class)
    public ApiRes<Res<String>> handleAdminCannotBeInstructorException(AdminCannotBeInstructorException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }
}
