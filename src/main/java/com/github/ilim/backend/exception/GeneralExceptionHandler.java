package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.BadRequestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

import static com.github.ilim.backend.util.ErrorUtil.prepMsg;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(prepMsg(e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(prepMsg(e.getMessage()));
    }

}
