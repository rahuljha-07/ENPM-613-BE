package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.AdminCannotBeInstructorException;
import com.github.ilim.backend.exception.exceptions.InstructorAppAlreadyExistsException;
import com.github.ilim.backend.exception.exceptions.InstructorAppNotFoundException;
import com.github.ilim.backend.exception.exceptions.UserAlreadyInstructorException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

import static com.github.ilim.backend.util.ErrorUtil.prepMsg;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class StudentExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(InstructorAppAlreadyExistsException.class)
    public ResponseEntity<String> handleInstructorAppAlreadyExistsException(InstructorAppAlreadyExistsException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(prepMsg(e.getMessage()));
    }

    @ExceptionHandler(InstructorAppNotFoundException.class)
    public ResponseEntity<String> InstructorAppNotFoundException(InstructorAppNotFoundException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(prepMsg(e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyInstructorException.class)
    public ResponseEntity<String> handleUserAlreadyInstructorException(UserAlreadyInstructorException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(prepMsg(e.getMessage()));
    }

    @ExceptionHandler(AdminCannotBeInstructorException.class)
    public ResponseEntity<String> handleAdminCannotBeInstructorException(AdminCannotBeInstructorException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(prepMsg(e.getMessage()));
    }
}
