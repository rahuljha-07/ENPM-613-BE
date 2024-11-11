package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.BadRequestException;
import com.github.ilim.backend.exception.exceptions.EmailSendingException;
import com.github.ilim.backend.exception.exceptions.NoPurchasesException;
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
public class GeneralExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiRes<Res<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warning(e.getMessage());
        return Reply.badRequest(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ApiRes<Res<String>> handleBadRequestException(BadRequestException e) {
        logger.warning(e.getMessage());
        return Reply.badRequest(e.getMessage());
    }

    @ExceptionHandler(EmailSendingException.class)
    public ApiRes<Res<String>> handleEmailSendingException(EmailSendingException e) {
        logger.severe("Email sending failed: " + e.getMessage());
        return Reply.badRequest("Failed to send support issue email. Please try again later.");
    }

    @ExceptionHandler(NoPurchasesException.class)
    public ApiRes<Res<String>> handleNoPurchasesException(NoPurchasesException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

}
