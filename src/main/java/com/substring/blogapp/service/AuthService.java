package com.substring.blogapp.service;

import com.substring.blogapp.dto.*;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    UserDto register(RegisterRequest registerRequest);

    UserDto getCurrentUser(String email);

    UserDto updateProfile(String email, UpdateProfileRequest request);
}
