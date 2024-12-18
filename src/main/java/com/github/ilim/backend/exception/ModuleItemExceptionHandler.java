package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.CantGenerateCertificateException;
import com.github.ilim.backend.exception.exceptions.ModuleItemNotFoundException;
import com.github.ilim.backend.exception.exceptions.VideoNotFoundException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ModuleItemExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(VideoNotFoundException.class)
    public ApiRes<Res<String>> handleVideoNotFoundException(VideoNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(ModuleItemNotFoundException.class)
    public ApiRes<Res<String>> handleModuleItemNotFoundException(ModuleItemNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(CantGenerateCertificateException.class)
    public ApiRes<Res<String>> handleCantGenerateCertificateException(CantGenerateCertificateException e) {
        logger.warning(e.getMessage());
        return Reply.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
