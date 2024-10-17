package com.github.ilim.backend.entity;

import com.github.ilim.backend.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;      // Cognito user 'sub' identifier

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthdate;

    private String profileImageUrl;

    @Column(nullable = false)
    private Role role = Role.STUDENT;

    private String schoolName;
    private String degreeTitle;
    private LocalDate graduateDate;

    private String professionalTitle;
    private int experienceYears;
    private String resumeUrl;

    private String teachingExperience;

    private String instructorTitle;
    private String instructorBio;

    private String videoApplication;

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
