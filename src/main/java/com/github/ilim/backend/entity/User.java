package com.github.ilim.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.ilim.backend.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;

import java.time.LocalDate;

/**
 * Entity representing a user in the system.
 * <p>
 * Contains user information such as email, name, birthdate, role, profile image URL, title, bio, and block status.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends AuditEntity {

    /**
     * The unique identifier of the user, corresponding to Cognito's 'sub' attribute.
     */
    @Id
    private String id;      // Cognito user 'sub' identifier

    /**
     * The user's email address. Must be unique and not null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The user's full name. Cannot be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The user's birthdate. Cannot be null.
     */
    @Column(nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthdate;

    /**
     * The role of the user within the system.
     * Defaults to {@link UserRole#STUDENT}.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.STUDENT;

    /**
     * The URL to the user's profile image.
     */
    private String profileImageUrl;

    /**
     * The user's professional title.
     */
    private String title;

    /**
     * A brief biography of the user.
     */
    @Column(length = 2000)
    private String bio;

    /**
     * Indicates whether the user is blocked from performing certain actions.
     * Defaults to {@code false}.
     */
    @Column(nullable = false)
    private boolean isBlocked = false;

    /**
     * Creates a {@link User} entity from a {@link AdminGetUserResponse} received from Cognito.
     *
     * @param cognitoUser the {@link AdminGetUserResponse} containing user attributes from Cognito
     * @return a new {@link User} entity populated with data from Cognito
     */
    public static User from(AdminGetUserResponse cognitoUser) {
        User user = new User();
        for (var attribute : cognitoUser.userAttributes()) {
            var val = attribute.value();
            switch (attribute.name()) {
                case "sub":
                    user.setId(val); break;
                case "email":
                    user.setEmail(val); break;
                case "name":
                    user.setName(val); break;
                case "birthdate":
                    user.setBirthdate(LocalDate.parse(val)); break;
            }
        }
        return user;
    }
}
