package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.CantFindCoursePurchaseException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.NoPurchasesException;
import com.github.ilim.backend.exception.exceptions.PendingPurchaseExistsException;
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
public class PurchaseExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(AlreadyPurchasedCourseException.class)
    public ApiRes<Res<String>> handleAlreadyPurchasedCourseException(AlreadyPurchasedCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(CantPurchaseOwnCourseException.class)
    public ApiRes<Res<String>> handleCantPurchaseOwnCourseException(CantPurchaseOwnCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(PendingPurchaseExistsException.class)
    public ApiRes<Res<String>> handlePendingPurchaseExistsException(PendingPurchaseExistsException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(NoPurchasesException.class)
    public ApiRes<Res<String>> handleNoPurchasesException(NoPurchasesException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(CantFindCoursePurchaseException.class)
    public ApiRes<Res<String>> handleCantFindCoursePurchaseException(CantFindCoursePurchaseException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

}
