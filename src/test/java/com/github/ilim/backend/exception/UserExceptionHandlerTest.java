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
import com.github.ilim.backend.util.response.Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserExceptionHandlerTest {

    private UserExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UserExceptionHandler();
    }

    @Test
    void handleUserNotFoundException() {
        String userId = "user-501";
        String expectedMessage = "User not found with ID: " + userId;
        UserNotFoundException exception = new UserNotFoundException(userId);
        ApiRes<Res<String>> response = handler.handleUserNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleMissingBirthdateException() {
        String expectedMessage = "Error: User Birthdate is Missing!";
        MissingBirthdateException exception = new MissingBirthdateException();
        ApiRes<Res<String>> response = handler.handleUserBirthdateMissing(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleMissingEmailOrPasswordException() {
        String expectedMessage = "Both email and password are required";
        MissingEmailOrPasswordException exception = new MissingEmailOrPasswordException();
        ApiRes<Res<String>> response = handler.handleMissingEmailOrPasswordException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleCantUpdateBlockedUserException() {
        String userId = "user-601";
        String expectedMessage = "Cant update blocked User(" + userId + ")!";
        CantUpdateBlockedUserException exception = new CantUpdateBlockedUserException(userId);
        ApiRes<Res<String>> response = handler.handleCantUpdateBlockedUserException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleUserIsNotAdminException() {
        String userId = "user-602";
        String expectedMessage = "User(" + userId + ") attempted to do an administration operation while user is not admin";
        UserIsNotAdminException exception = new UserIsNotAdminException(userId);
        ApiRes<Res<String>> response = handler.handleUserIsNotAdminException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleUserIsAlreadyBlockedException() {
        String userId = "user-603";
        String expectedMessage = "Cannot block User(" + userId + ") because it's already blocked!";
        UserIsAlreadyBlockedException exception = new UserIsAlreadyBlockedException(userId);
        ApiRes<Res<String>> response = handler.handleUserIsAlreadyBlockedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleBlockedUserCantSignInException() {
        String email = "blocked.user@example.com";
        String expectedMessage = "User(" + email + ") cannot sign in because user is blocked.";
        BlockedUserCantSignInException exception = new BlockedUserCantSignInException(email);
        ApiRes<Res<String>> response = handler.handleBlockedUserCantSignInException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }

    @Test
    void handleAdminCantBeBlockedException() {
        String userId = "admin-701";
        String expectedMessage = "User(" + userId + ") has role ADMIN, and admins can not be blocked.";
        AdminCantBeBlockedException exception = new AdminCantBeBlockedException(userId);
        ApiRes<Res<String>> response = handler.handleAdminCantBeBlockedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().body());
    }
}
