package com.github.ilim.backend.auth;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.exception.exceptions.UserNotFoundException;
import com.github.ilim.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new CustomUserDetailsService(userService);
    }

    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setId("user123");
        user.setRole(UserRole.STUDENT);

        when(userService.findById("user123")).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("user123");

        assertNotNull(userDetails);
        assertEquals("user123", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userService.findById("nonexistent")).thenThrow(new UserNotFoundException("nonexistent"));

        assertThrows(UserNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
    }
}
