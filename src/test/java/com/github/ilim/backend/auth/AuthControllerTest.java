package com.github.ilim.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.ChangePasswordDto;
import com.github.ilim.backend.dto.ResetPasswordDto;
import com.github.ilim.backend.dto.SignInDto;
import com.github.ilim.backend.dto.SignUpDto;
import com.github.ilim.backend.dto.VerifyAccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    void setUp() {
        // Initialize any required setup before each test
    }

    @Test
    void testSignUp() throws Exception {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        signUpDto.setName("Test User");
        signUpDto.setBirthdate(LocalDate.now());

        mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testVerifyAccount() throws Exception {
        VerifyAccountDto verifyAccountDto = new VerifyAccountDto();
        verifyAccountDto.setEmail("test@example.com");
        verifyAccountDto.setConfirmationCode("123456");

        mockMvc.perform(post("/auth/verify-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyAccountDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testSignIn() throws Exception {
        SignInDto signInDto = new SignInDto();
        signInDto.setEmail("test@example.com");
        signInDto.setPassword("password");

        AdminInitiateAuthResponse authResponse = AdminInitiateAuthResponse.builder()
            .authenticationResult(AuthenticationResultType.builder()
                .accessToken("access_token")
                .idToken("id_token")
                .refreshToken("refresh_token")
                .expiresIn(3600)
                .build())
            .build();

        when(authService.signIn(any(String.class), any(String.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testForgotPassword() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(email)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testResetPassword() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setEmail("test@example.com");
        resetPasswordDto.setConfirmationCode("123456");
        resetPasswordDto.setNewPassword("new_password");

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    void testChangePassword() throws Exception {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("old_password");
        changePasswordDto.setNewPassword("new_password");

        mockMvc.perform(post("/auth/change-password")
                .header("Authorization", "Bearer access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDto)))
            .andExpect(status().isForbidden());
    }
}
