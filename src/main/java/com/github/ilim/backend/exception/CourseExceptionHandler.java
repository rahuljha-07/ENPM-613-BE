package com.github.ilim.backend.exception;


import com.github.ilim.backend.exception.exceptions.AccessDeletedCourseException;
import com.github.ilim.backend.exception.exceptions.AlreadyPurchasedCourseException;
import com.github.ilim.backend.exception.exceptions.CantCreatePublicCourseException;
import com.github.ilim.backend.exception.exceptions.CantPurchaseOwnCourseException;
import com.github.ilim.backend.exception.exceptions.CourseAlreadyPublished;
import com.github.ilim.backend.exception.exceptions.CourseIsNotWaitingApprovalException;
import com.github.ilim.backend.exception.exceptions.CourseNotFoundException;
import com.github.ilim.backend.exception.exceptions.CourseStatusNotDraftException;
import com.github.ilim.backend.exception.exceptions.NoAccessToCourseContentException;
import com.github.ilim.backend.exception.exceptions.OnlyAdminAccessAllCourses;
import com.github.ilim.backend.exception.exceptions.StudentDidNotCompleteCourseException;
import com.github.ilim.backend.exception.exceptions.UserCannotCreateCourseException;
import com.github.ilim.backend.exception.exceptions.UserCantHaveQuizProgress;
import com.github.ilim.backend.exception.exceptions.UserHasNoAccessToCourseException;
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
public class CourseExceptionHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(UserCannotCreateCourseException.class)
    public ApiRes<Res<String>> handleUserCannotCreateCourseException(UserCannotCreateCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(UserHasNoAccessToCourseException.class)
    public ApiRes<Res<String>> handleUserHasNoAccessToCourseException(UserHasNoAccessToCourseException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(NoAccessToCourseContentException.class)
    public ApiRes<Res<String>> handleNoAccessToCourseContentException(NoAccessToCourseContentException e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ApiRes<Res<String>> handleCourseNotFoundException(CourseNotFoundException e) {
        logger.warning(e.getMessage());
        return Reply.notFound(e.getMessage());
    }

    @ExceptionHandler(AccessDeletedCourseException.class)
    public ApiRes<Res<String>> handleAccessDeletedCourseException(AccessDeletedCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

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

    @ExceptionHandler(OnlyAdminAccessAllCourses.class)
    public ApiRes<Res<String>> handleOnlyAdminAccessAllCourses(OnlyAdminAccessAllCourses e) {
        logger.warning(e.getMessage());
        return Reply.unauthorized(e.getMessage());
    }

    @ExceptionHandler(UserCantHaveQuizProgress.class)
    public ApiRes<Res<String>> handleUserCantHaveQuizProgress(UserCantHaveQuizProgress e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(StudentDidNotCompleteCourseException.class)
    public ApiRes<Res<String>> handleStudentDidNotCompleteCourseException(StudentDidNotCompleteCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(CourseAlreadyPublished.class)
    public ApiRes<Res<String>> handleCourseAlreadyPublished(CourseAlreadyPublished e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(CourseStatusNotDraftException.class)
    public ApiRes<Res<String>> handleCourseStatusNotDraftException(CourseStatusNotDraftException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(CourseIsNotWaitingApprovalException.class)
    public ApiRes<Res<String>> handleCourseIsNotWaitingApprovalException(CourseIsNotWaitingApprovalException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

    @ExceptionHandler(CantCreatePublicCourseException.class)
    public ApiRes<Res<String>> handleCantCreatePublicCourseException(CantCreatePublicCourseException e) {
        logger.warning(e.getMessage());
        return Reply.forbidden(e.getMessage());
    }

}
