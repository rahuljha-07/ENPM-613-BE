package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.MissingBirthdateException;
import com.github.ilim.backend.exception.exceptions.MissingEmailOrPasswordException;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static com.github.ilim.backend.util.ErrorUtil.cleanMessage;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class UserExceptionHandler {
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<String> handleUsernameExistsException(UsernameExistsException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleInvalidParameterException(InvalidParameterException e) {
        logger.warning(e.getMessage());
        var message = e.getMessage().replaceAll("username", "email");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cleanMessage(message));
    }

    @ExceptionHandler(MissingBirthdateException.class)
    public ResponseEntity<String> handleUserBirthdateMissing(MissingBirthdateException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(UserNotConfirmedException.class)
    public ResponseEntity<String> handleUserNotConfirmedException(UserNotConfirmedException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<String> handleNotAuthorizedException(NotAuthorizedException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(MissingEmailOrPasswordException.class)
    public ResponseEntity<String> handleMissingEmailOrPasswordException(MissingEmailOrPasswordException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(ExpiredCodeException.class)
    public ResponseEntity<String> handleExpiredCodeException(ExpiredCodeException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cleanMessage(e.getMessage()) + " Or invalid email");
    }

    @ExceptionHandler(CodeMismatchException.class)
    public ResponseEntity<String> handleCodeMismatchException(CodeMismatchException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cleanMessage(e.getMessage()));
    }

    @ExceptionHandler(LimitExceededException.class)
    public ResponseEntity<String> handleLimitExceededException(LimitExceededException e) {
        logger.warning(e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(cleanMessage(e.getMessage()));
    }
}