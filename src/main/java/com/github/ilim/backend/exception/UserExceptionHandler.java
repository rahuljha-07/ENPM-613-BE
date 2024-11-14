package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.AdminCantBeBlockedException;
import com.github.ilim.backend.exception.exceptions.BlockedUserCantSignInException;
import com.github.ilim.backend.exception.exceptions.CantUpdateBlockedUserException;
import com.github.ilim.backend.exception.exceptions.MissingBirthdateException;
import com.github.ilim.backend.exception.exceptions.MissingEmailOrPasswordException;
import com.github.ilim.backend.exception.exceptions.UserIsAlreadyBlockedException;
import com.github.ilim.backend.exception.exceptions.UserIsNotAdminException;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CodeMismatchException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ExpiredCodeException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.LimitExceededException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import java.util.logging.Logger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class UserExceptionHandler {
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(UserNotFoundException.class)
    public ApiRes<Res<String>> handleUserNotFoundException(UserNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ApiRes<Res<String>> handleUsernameExistsException(UsernameExistsException e) {
        logger.warning(e.getMessage());
        return Reply.conflict("User with this email already exists");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ApiRes<Res<String>> handleInvalidPasswordException(InvalidPasswordException e) {
        logger.warning(e.getMessage());
        return Reply.create(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ApiRes<Res<String>> handleInvalidParameterException(InvalidParameterException e) {
        logger.warning(e.getMessage());
        var message = e.getMessage().replaceAll("username", "email");
        return Reply.badRequest(message);
    }

    @ExceptionHandler(MissingBirthdateException.class)
    public ApiRes<Res<String>> handleUserBirthdateMissing(MissingBirthdateException e) {
        logger.warning(e.getMessage());
        return Reply.badRequest(e.getMessage());
    }

    @ExceptionHandler(UserNotConfirmedException.class)
    public ApiRes<Res<String>> handleUserNotConfirmedException(UserNotConfirmedException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ApiRes<Res<String>> handleNotAuthorizedException(NotAuthorizedException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(MissingEmailOrPasswordException.class)
    public ApiRes<Res<String>> handleMissingEmailOrPasswordException(MissingEmailOrPasswordException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(ExpiredCodeException.class)
    public ApiRes<Res<String>> handleExpiredCodeException(ExpiredCodeException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage() + " Or invalid email");
    }

    @ExceptionHandler(CodeMismatchException.class)
    public ApiRes<Res<String>> handleCodeMismatchException(CodeMismatchException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(LimitExceededException.class)
    public ApiRes<Res<String>> handleLimitExceededException(LimitExceededException e) {
        logger.warning(e.getMessage());
        return Reply.create(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }

    @ExceptionHandler(CantUpdateBlockedUserException.class)
    public ApiRes<Res<String>> handleCantUpdateBlockedUserException(CantUpdateBlockedUserException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(UserIsNotAdminException.class)
    public ApiRes<Res<String>> handleUserIsNotAdminException(UserIsNotAdminException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(UserIsAlreadyBlockedException.class)
    public ApiRes<Res<String>> handleUserIsAlreadyBlockedException(UserIsAlreadyBlockedException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(BlockedUserCantSignInException.class)
    public ApiRes<Res<String>> handleBlockedUserCantSignInException(BlockedUserCantSignInException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(AdminCantBeBlockedException.class)
    public ApiRes<Res<String>> handleAdminCantBeBlockedException(AdminCantBeBlockedException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }
}