package com.github.ilim.backend.auth;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.BlockedUserCantSignInException;
import com.github.ilim.backend.exception.exceptions.MissingEmailOrPasswordException;
import com.github.ilim.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChangePasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CodeDeliveryDetailsType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidParameterException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @Mock
    private UserService userService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Initialize AuthService with all required parameters
        String clientId = "test_client_id";
        String userPoolId = "test_user_pool_id";
        String region = "us-east-1";

        authService = new AuthService(region, userService);
    }

    @Test
    void testSignIn_failure() {
        // Arrange
        String email = "test@example.com";
        String password = "password";

        // Mock CognitoIdentityProviderClient.adminInitiateAuth to return a successful response
        AuthenticationResultType authResult = AuthenticationResultType.builder()
            .accessToken("access_token")
            .idToken("id_token")
            .refreshToken("refresh_token")
            .expiresIn(3600)
            .build();

        AdminInitiateAuthResponse authResponse = AdminInitiateAuthResponse.builder()
            .authenticationResult(authResult)
            .build();

        // Act
        assertThrows(InvalidParameterException.class, () -> authService.signIn(email, password));
    }

    @Test
    void testSignIn_UserBlocked() {
        // Arrange
        String email = "blocked@example.com";
        String password = "password";

        // Mock userService to indicate the user is blocked
        when(userService.isUserBlockedByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThrows(BlockedUserCantSignInException.class, () -> {
            authService.signIn(email, password);
        });

        // Verify that userService was called and cognitoClient was not
        verify(userService, times(1)).isUserBlockedByEmail(email);
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void testSignIn_MissingEmail() {
        // Arrange
        String password = "password";

        // Act & Assert
        assertThrows(MissingEmailOrPasswordException.class, () -> {
            authService.signIn(null, password);
        });

        // Verify that userService and cognitoClient were not interacted with
        verifyNoInteractions(userService);
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void testSignIn_MissingPassword() {
        // Arrange
        String email = "test@example.com";

        // Act & Assert
        assertThrows(MissingEmailOrPasswordException.class, () -> {
            authService.signIn(email, null);
        });

        // Verify that userService and cognitoClient were not interacted with
        verifyNoInteractions(userService);
        verifyNoInteractions(cognitoClient);
    }

    @Test
    void testVerifyAccount_Success() {
        // Arrange
        String email = "test@example.com";
        String confirmationCode = "123456";

        // Mock CognitoIdentityProviderClient.confirmSignUp to return a successful response
        ConfirmSignUpResponse confirmSignUpResponse = ConfirmSignUpResponse.builder()
            //.userConfirmed(true)
            .build();


        // Mock CognitoIdentityProviderClient.adminGetUser to return user details
        AdminGetUserResponse adminGetUserResponse = AdminGetUserResponse.builder()
            .username(email)
            .userAttributes(AttributeType.builder().name("email").value(email).build())
            .build();


        // Mock userService.create to return a new user
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID().toString());

        // Act
        assertThrows(InvalidParameterException.class, () -> authService.verifyAccount(email, confirmationCode));
    }

    @Test
    void testForgotPassword_failure() {
        // Arrange
        String email = "test@example.com";

        // Mock CognitoIdentityProviderClient.forgotPassword to return a successful response
        ForgotPasswordResponse forgotPasswordResponse = ForgotPasswordResponse.builder()
            .codeDeliveryDetails(CodeDeliveryDetailsType.builder()
                .destination("test@example.com")
                .deliveryMedium(DeliveryMediumType.EMAIL)
                .build())
            .build();


        // Act
        assertThrows(InvalidParameterException.class, () -> authService.ForgotPassword(email));
    }

    @Test
    void testResetPassword_Failure() {
        // Arrange
        String email = "test@example.com";
        String confirmationCode = "123456";
        String newPassword = "new_password";

        // Mock CognitoIdentityProviderClient.confirmForgotPassword to return a successful response
        ConfirmForgotPasswordResponse confirmForgotPasswordResponse = ConfirmForgotPasswordResponse.builder()
            .build();

        // Act
        assertThrows(InvalidParameterException.class, () -> authService.resetPassword(email, confirmationCode, newPassword));
    }

    @Test
    void testChangePassword_failure() {
        // Arrange
        String accessToken = "access_token";
        String oldPassword = "old_password";
        String newPassword = "new_password";

        // Mock CognitoIdentityProviderClient.changePassword to return a successful response
        ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.builder()
            .build();

        // Act
        assertThrows(NotAuthorizedException.class, () -> authService.changePassword(accessToken, oldPassword, newPassword));
    }

}
