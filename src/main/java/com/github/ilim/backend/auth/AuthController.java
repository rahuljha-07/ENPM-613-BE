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

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiRes<Res<String>> signUp(@Valid @RequestBody SignUpDto signUpRequest) {
        authService.signUp(signUpRequest);
        return Reply.created("Sign-up successful. Please verify your email.");
    }

    @PostMapping("/verify-account")
    public ApiRes<Res<String>> verifyAccount(@Valid @RequestBody VerifyAccountDto dto) {
        authService.verifyAccount(dto.getEmail(), dto.getConfirmationCode());
        return Reply.ok("User confirmed successfully.");
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signIn(@Valid @RequestBody SignInDto signInRequest) {
        var authResponse = authService.signIn(signInRequest.getEmail(), signInRequest.getPassword());

        var response = new AuthResponseDto();
        response.setAccessToken(authResponse.authenticationResult().accessToken());
        response.setIdToken(authResponse.authenticationResult().idToken());
        response.setRefreshToken(authResponse.authenticationResult().refreshToken());
        response.setExpiresIn(authResponse.authenticationResult().expiresIn());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ApiRes<Res<String>> forgotPassword(@Valid @RequestBody String email) {
        authService.ForgotPassword(email);
        return Reply.ok("Password reset initiated. Check your email for the verification code.");
    }

    @PostMapping("/reset-password")
    public ApiRes<Res<String>> confirmForgotPassword(@Valid @RequestBody ResetPasswordDto dto) {
        authService.resetPassword(
            dto.getEmail(),
            dto.getConfirmationCode(),
            dto.getNewPassword()
        );
        return Reply.ok("Password has been reset successfully.");
    }

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

