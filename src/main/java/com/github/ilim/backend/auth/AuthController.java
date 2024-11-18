package com.github.ilim.backend.auth;

import com.github.ilim.backend.dto.AuthResponseDto;
import com.github.ilim.backend.dto.ChangePasswordDto;
import com.github.ilim.backend.dto.ResetPasswordDto;
import com.github.ilim.backend.dto.SignInDto;
import com.github.ilim.backend.dto.SignUpDto;
import com.github.ilim.backend.dto.VerifyAccountDto;
import com.github.ilim.backend.util.response.ApiRes;
import com.github.ilim.backend.util.response.Reply;
import com.github.ilim.backend.util.response.Res;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for handling authentication-related endpoints.
 * <p>
 * Provides endpoints for user sign-up, account verification, sign-in, password reset,
 * and password change functionalities.
 * </p>
 *
 * @author
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Handles user sign-up requests.
     * <p>
     * Accepts a {@link SignUpDto} containing user registration details, invokes the
     * {@link AuthService#signUp(SignUpDto)} method, and returns a response indicating
     * successful sign-up.
     * </p>
     *
     * @param signUpRequest the sign-up request payload containing user details
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/sign-up")
    public ApiRes<Res<String>> signUp(@Valid @RequestBody SignUpDto signUpRequest) {
        authService.signUp(signUpRequest);
        return Reply.created("Sign-up successful. Please verify your email.");
    }

    /**
     * Handles account verification requests.
     * <p>
     * Accepts a {@link VerifyAccountDto} containing the user's email and confirmation code,
     * invokes the {@link AuthService#verifyAccount(String, String)} method, and returns
     * a response indicating successful account verification.
     * </p>
     *
     * @param dto the account verification request payload containing email and confirmation code
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/verify-account")
    public ApiRes<Res<String>> verifyAccount(@Valid @RequestBody VerifyAccountDto dto) {
        authService.verifyAccount(dto.getEmail(), dto.getConfirmationCode());
        return Reply.ok("User confirmed successfully.");
    }

    /**
     * Handles user sign-in requests.
     * <p>
     * Accepts a {@link SignInDto} containing user credentials, invokes the
     * {@link AuthService#signIn(String, String)} method, and returns an
     * {@link AuthResponseDto} containing authentication tokens.
     * </p>
     *
     * @param signInRequest the sign-in request payload containing email and password
     * @return a {@link ResponseEntity} containing the authentication response DTO
     */
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> signIn(@Valid @RequestBody SignInDto signInRequest) {
        var authResponse = authService.signIn(signInRequest.getEmail(), signInRequest.getPassword());

        var response = new AuthResponseDto();
        response.setAccessToken(authResponse.authenticationResult().accessToken());
        response.setIdToken(authResponse.authenticationResult().idToken());
        response.setRefreshToken(authResponse.authenticationResult().refreshToken());
        response.setExpiresIn(authResponse.authenticationResult().expiresIn());
        return ResponseEntity.ok(response);
    }

    /**
     * Initiates the password reset process for a user.
     * <p>
     * Accepts an email address as a {@link String}, invokes the
     * {@link AuthService#ForgotPassword(String)} method, and returns a response indicating
     * that the password reset has been initiated.
     * </p>
     *
     * @param email the email address of the user requesting a password reset
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/forgot-password")
    public ApiRes<Res<String>> forgotPassword(@Valid @RequestBody String email) {
        authService.ForgotPassword(email);
        return Reply.ok("Password reset initiated. Check your email for the verification code.");
    }

    /**
     * Completes the password reset process by confirming the reset with a verification code.
     * <p>
     * Accepts a {@link ResetPasswordDto} containing the user's email, confirmation code,
     * and new password, invokes the {@link AuthService#resetPassword(String, String, String)}
     * method, and returns a response indicating successful password reset.
     * </p>
     *
     * @param dto the password reset confirmation payload containing email, confirmation code, and new password
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/reset-password")
    public ApiRes<Res<String>> confirmForgotPassword(@Valid @RequestBody ResetPasswordDto dto) {
        authService.resetPassword(
            dto.getEmail(),
            dto.getConfirmationCode(),
            dto.getNewPassword()
        );
        return Reply.ok("Password has been reset successfully.");
    }

    /**
     * Allows an authenticated user to change their password.
     * <p>
     * Accepts a {@link ChangePasswordDto} containing the old and new passwords, extracts the
     * access token from the {@code Authorization} header, invokes the
     * {@link AuthService#changePassword(String, String, String)} method, and returns a
     * response indicating successful password change.
     * </p>
     *
     * @param requestDto          the password change request payload containing old and new passwords
     * @param authorizationHeader the {@code Authorization} header containing the Bearer token
     * @return an {@link ApiRes} containing a success message
     */
    @PostMapping("/change-password")
    public ApiRes<Res<String>> changePassword(
        @Valid @RequestBody ChangePasswordDto requestDto,
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        authService.changePassword(accessToken, requestDto.getOldPassword(), requestDto.getNewPassword());
        return Reply.ok("Password changed successfully.");
    }

}
