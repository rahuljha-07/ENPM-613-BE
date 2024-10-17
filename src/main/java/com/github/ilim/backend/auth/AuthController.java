package com.github.ilim.backend.auth;

import com.github.ilim.backend.dto.AuthResponseDto;
import com.github.ilim.backend.dto.ChangePasswordDto;
import com.github.ilim.backend.dto.ResetPasswordDto;
import com.github.ilim.backend.dto.SignInDto;
import com.github.ilim.backend.dto.SignUpDto;
import com.github.ilim.backend.dto.VerifyAccountDto;
import com.github.ilim.backend.service.UserService;
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
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.ok("Sign-up successful. Please verify your email.");
    }

    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestBody VerifyAccountDto dto) {
        authService.verifyAccount(dto.getEmail(), dto.getConfirmationCode());
        return ResponseEntity.ok("User confirmed successfully.");
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signIn(@RequestBody SignInDto signInRequest) {
        var authResponse = authService.signIn(signInRequest.getEmail(), signInRequest.getPassword());

        var response = new AuthResponseDto();
        response.setAccessToken(authResponse.authenticationResult().accessToken());
        response.setIdToken(authResponse.authenticationResult().idToken());
        response.setRefreshToken(authResponse.authenticationResult().refreshToken());
        response.setExpiresIn(authResponse.authenticationResult().expiresIn());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        authService.ForgotPassword(email);
        return ResponseEntity.ok("Password reset initiated. Check your email for the verification code.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> confirmForgotPassword(@RequestBody ResetPasswordDto dto) {
        authService.resetPassword(
            dto.getEmail(),
            dto.getConfirmationCode(),
            dto.getNewPassword()
        );
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
        @RequestBody ChangePasswordDto requestDto,
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        authService.changePassword(accessToken, requestDto.getOldPassword(), requestDto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully.");
    }
}

