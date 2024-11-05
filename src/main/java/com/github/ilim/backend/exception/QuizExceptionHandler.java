package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.CantAttemptOwnQuizException;
import com.github.ilim.backend.exception.exceptions.CantDeleteAttemptedQuizException;
import com.github.ilim.backend.exception.exceptions.MissingAnswerException;
import com.github.ilim.backend.exception.exceptions.QuestionOptionNotFoundException;
import com.github.ilim.backend.exception.exceptions.QuizAttemptsNotFoundException;
import com.github.ilim.backend.exception.exceptions.QuizNotFoundException;
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
public class QuizExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(QuizNotFoundException.class)
    public ApiRes<Res<String>> handleQuizNotFoundException(QuizNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(MissingAnswerException.class)
    public ApiRes<Res<String>> handleMissingAnswerException(MissingAnswerException e) {
        logger.warning(e.getMessage());
        return Reply.badRequest(e.getMessage());
    }

    @ExceptionHandler(QuestionOptionNotFoundException.class)
    public ApiRes<Res<String>> handleQuestionOptionNotFoundException(QuestionOptionNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(CantAttemptOwnQuizException.class)
    public ApiRes<Res<String>> handleCantAttemptOwnQuizException(CantAttemptOwnQuizException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(CantDeleteAttemptedQuizException.class)
    public ApiRes<Res<String>> handleCantDeleteAttemptedQuizException(CantDeleteAttemptedQuizException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(QuizAttemptsNotFoundException.class)
    public ApiRes<Res<String>> handleQuizAttemptNoteFoundException(QuizAttemptsNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

}
