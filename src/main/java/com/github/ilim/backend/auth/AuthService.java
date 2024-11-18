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

/**
 * Service class responsible for handling authentication-related operations using AWS Cognito.
 * <p>
 * Provides functionalities such as user sign-in, sign-up, account verification, password reset,
 * and password change by interacting with AWS Cognito Identity Provider.
 * </p>
 *
 * @author
 */
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

    /**
     * Constructs an instance of {@code AuthService} with the specified AWS region and user service.
     * <p>
     * Initializes the {@link CognitoIdentityProviderClient} with the provided region and default credentials.
     * </p>
     *
     * @param region      the AWS region for Cognito services
     * @param userService the user service for managing user-related operations
     */
    public AuthService(@Value("${aws.cognito.region}") String region, UserService userService) {
        this.cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
        this.userService = userService;
    }

    /**
     * Authenticates a user with the provided email and password.
     * <p>
     * Validates the input credentials, checks if the user is blocked, and initiates the authentication
     * process with AWS Cognito.
     * </p>
     *
     * @param email    the user's email address
     * @param password the user's password
     * @return an {@link AdminInitiateAuthResponse} containing authentication tokens
     * @throws MissingEmailOrPasswordException if the email or password is missing or blank
     * @throws BlockedUserCantSignInException if the user is blocked from signing in
     */
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

    /**
     * Registers a new user using the provided sign-up data.
     * <p>
     * Constructs a {@link SignUpRequest} with the user's details and sends it to AWS Cognito for registration.
     * </p>
     *
     * @param dto the sign-up data transfer object containing user registration details
     */
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

    /**
     * Verifies a user's account using their email and confirmation code.
     * <p>
     * Sends a {@link ConfirmSignUpRequest} to AWS Cognito to confirm the user's account and subsequently
     * saves the user in the application's database.
     * </p>
     *
     * @param email            the user's email address
     * @param confirmationCode the confirmation code sent to the user's email
     */
    public void verifyAccount(String email, String confirmationCode) {
        var request = ConfirmSignUpRequest.builder()
            .clientId(clientId)
            .username(email)
            .confirmationCode(confirmationCode)
            .build();

        cognitoClient.confirmSignUp(request);
        saveUserInDatabase(email);
    }

    /**
     * Saves the authenticated user in the application's database.
     * <p>
     * Retrieves the user information from AWS Cognito using the provided email and creates a corresponding
     * user entity in the application's database.
     * </p>
     *
     * @param email the user's email address
     */
    private void saveUserInDatabase(String email) {
        var getUserRequest = AdminGetUserRequest.builder() // from Cognito
            .userPoolId(userPoolId)
            .username(email)
            .build();

        var cognitoUser = cognitoClient.adminGetUser(getUserRequest);
        User user = User.from(cognitoUser);
        userService.create(user);
    }

    /**
     * Initiates the password reset process for a user.
     * <p>
     * Sends a {@link ForgotPasswordRequest} to AWS Cognito to initiate the password reset process.
     * </p>
     *
     * @param email the user's email address requesting a password reset
     */
    public void ForgotPassword(String email) {
        var request = ForgotPasswordRequest.builder()
            .clientId(clientId)
            .username(email)
            .build();

        cognitoClient.forgotPassword(request);
    }

    /**
     * Completes the password reset process by confirming the reset with a verification code.
     * <p>
     * Sends a {@link ConfirmForgotPasswordRequest} to AWS Cognito to set a new password for the user.
     * </p>
     *
     * @param email           the user's email address
     * @param confirmationCode the confirmation code sent to the user's email
     * @param newPassword     the new password to be set for the user
     */
    public void resetPassword(String email, String confirmationCode, String newPassword) {
        var request = ConfirmForgotPasswordRequest.builder()
            .clientId(clientId)
            .username(email)
            .confirmationCode(confirmationCode)
            .password(newPassword)
            .build();

        cognitoClient.confirmForgotPassword(request);
    }

    /**
     * Changes the password for an authenticated user.
     * <p>
     * Sends a {@link ChangePasswordRequest} to AWS Cognito to update the user's password.
     * </p>
     *
     * @param accessToken       the access token of the authenticated user
     * @param previousPassword  the user's current password
     * @param proposedPassword  the new password to be set
     */
    public void changePassword(String accessToken, String previousPassword, String proposedPassword) {
        var request = ChangePasswordRequest.builder()
            .accessToken(accessToken)
            .previousPassword(previousPassword)
            .proposedPassword(proposedPassword)
            .build();

        cognitoClient.changePassword(request);
    }
}
