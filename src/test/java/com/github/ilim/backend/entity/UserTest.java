package com.github.ilim.backend.entity;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testFromCognitoUser() {
        AdminGetUserResponse cognitoUser = AdminGetUserResponse.builder()
            .userAttributes(Arrays.asList(
                AttributeType.builder().name("sub").value("user-123").build(),
                AttributeType.builder().name("email").value("user@example.com").build(),
                AttributeType.builder().name("name").value("John Doe").build(),
                AttributeType.builder().name("birthdate").value("1990-01-01").build()
            ))
            .build();

        User user = User.from(cognitoUser);

        assertEquals("user-123", user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
        assertEquals(LocalDate.parse("1990-01-01"), user.getBirthdate());
    }
}
