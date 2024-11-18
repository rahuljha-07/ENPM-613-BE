package com.github.ilim.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ilim.backend.dto.UpdateUserDto;
import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.enums.UserRole;
import com.github.ilim.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import({UserController.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Mock the WebClient.Builder bean
    @MockBean
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        // Create a user
        user = new User();
        user.setId("user123");
        user.setEmail("user@example.com");
        user.setName("User Name");
        user.setRole(UserRole.STUDENT);

        updateUserDto = new UpdateUserDto();
        updateUserDto.setBio("Updated Bio");
        updateUserDto.setTitle("Updated Title");
        updateUserDto.setProfileImageUrl("http://example.com/profile.jpg");
    }

    @Test
    void testGetUserProfile() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/user")
                .with(jwt().jwt(jwt -> jwt.claim("sub", user.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body.id").value(user.getId()))
            .andExpect(jsonPath("$.body.email").value(user.getEmail()));

        verify(userService, times(1)).findById(user.getId());
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDto))
                .with(jwt().jwt(jwt -> jwt.claim("sub", user.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.body").value("User data has been updated successfully"));

        verify(userService, times(1)).updateFromDto(user, updateUserDto);
    }
}
