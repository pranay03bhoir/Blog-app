package com.substring.blogapp.service;

import com.substring.blogapp.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testRegisterAndLogin() {
        String testEmail = "developer" + System.currentTimeMillis() + "@test.com";

        RegisterRequest registerRequest = new RegisterRequest(
                "Test Developer",
                testEmail,
                "secretPassword123",
                "Full-stack engineer",
                "Building cool stuff"
        );

        UserDto registered = authService.register(registerRequest);
        assertNotNull(registered.getId());
        assertEquals("Test Developer", registered.getName());
        assertEquals(testEmail, registered.getEmail());

        // Login
        LoginRequest loginRequest = new LoginRequest(testEmail, "secretPassword123");
        AuthResponse authResponse = authService.login(loginRequest);

        assertNotNull(authResponse.getToken());
        assertEquals("Bearer", authResponse.getTokenType());
        assertEquals(testEmail, authResponse.getUser().getEmail());
    }

    @Test
    void testUpdateProfile() {
        String testEmail = "author" + System.currentTimeMillis() + "@test.com";

        authService.register(new RegisterRequest("Original Name", testEmail, "pass123", "Bio", "Tagline"));

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setTagline("Updated Tagline");
        updateRequest.setBio("Updated Bio");

        UserDto updated = authService.updateProfile(testEmail, updateRequest);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Tagline", updated.getTagline());
        assertEquals("Updated Bio", updated.getBio());
    }
}
