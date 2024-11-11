package com.github.ilim.backend.auth;

import com.github.ilim.backend.dto.SignUpDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.exception.exceptions.BlockedUserCantSignInException;
import com.github.ilim.backend.exception.exceptions.MissingEmailOrPasswordException;
import com.github.ilim.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChangePasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

import java.util.Map;

@Service
public class AuthService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final UserService userService;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.region}")
    private String region;

    public AuthService(@Value("${aws.cognito.region}") String region, UserService userService) {
        this.cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
        this.userService = userService;
    }

    public AdminInitiateAuthResponse signIn(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new MissingEmailOrPasswordException();
        }
        if (userService.isUserBlockedByEmail(email)) {
            throw new BlockedUserCantSignInException(email);
        }
        var authRequest = AdminInitiateAuthRequest.builder()
            .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
            .userPoolId(userPoolId)
            .clientId(clientId)
            .authParameters(Map.of(
                "USERNAME", email,
                "PASSWORD", password
            ))
            .build();
        return cognitoClient.adminInitiateAuth(authRequest);
    }

    public void signUp(SignUpDto dto) {
        try (var unauthenticatedClient = CognitoIdentityProviderClient.builder()
            .region(Region.of(region))
            .build()
        ) {
            var request = SignUpRequest.builder()
                .clientId(clientId)
                .username(dto.getEmail())
                .password(dto.getPassword())
                .userAttributes(
                    AttributeType.builder().name("name").value(dto.getName()).build(),
                    AttributeType.builder().name("birthdate").value(dto.getBirthdateString()).build()
                )
                .build();

            unauthenticatedClient.signUp(request);
        }
    }

    public void verifyAccount(String email, String confirmationCode) {
        var request = ConfirmSignUpRequest.builder()
            .clientId(clientId)
            .username(email)
            .confirmationCode(confirmationCode)
            .build();

        cognitoClient.confirmSignUp(request);
        saveUserInDatabase(email);
    }

    private void saveUserInDatabase(String email) {
        var getUserRequest = AdminGetUserRequest.builder() // from Cognito
            .userPoolId(userPoolId)
            .username(email)
            .build();

        var cognitoUser = cognitoClient.adminGetUser(getUserRequest);
        User user = User.from(cognitoUser);
        userService.create(user);
    }

    public void ForgotPassword(String email) {
        var request = ForgotPasswordRequest.builder()
            .clientId(clientId)
            .username(email)
            .build();

        cognitoClient.forgotPassword(request);
    }

    public void resetPassword(String email, String confirmationCode, String newPassword) {
        var request = ConfirmForgotPasswordRequest.builder()
            .clientId(clientId)
            .username(email)
            .confirmationCode(confirmationCode)
            .password(newPassword)
            .build();

        cognitoClient.confirmForgotPassword(request);
    }

    public void changePassword(String accessToken, String previousPassword, String proposedPassword) {
        var request = ChangePasswordRequest.builder()
            .accessToken(accessToken)
            .previousPassword(previousPassword)
            .proposedPassword(proposedPassword)
            .build();

        cognitoClient.changePassword(request);
    }
}

