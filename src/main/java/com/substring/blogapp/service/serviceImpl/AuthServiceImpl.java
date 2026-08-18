package com.substring.blogapp.service.serviceImpl;

import com.substring.blogapp.dto.*;
import com.substring.blogapp.exceptions.AlreadyExistsException;
import com.substring.blogapp.exceptions.ResourceNotFoundException;
import com.substring.blogapp.models.Role;
import com.substring.blogapp.models.User;
import com.substring.blogapp.repositories.ArticleRepository;
import com.substring.blogapp.repositories.UserRepository;
import com.substring.blogapp.security.jwt.JwtTokenProvider;
import com.substring.blogapp.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    public AuthServiceImpl(UserRepository userRepository, ArticleRepository articleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDto userDto = mapToUserDto(user);

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .user(userDto)
                .expiresInMs(tokenProvider.getExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public UserDto register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AlreadyExistsException("An account with email " + registerRequest.getEmail() + " already exists.");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setBio(registerRequest.getBio());
        user.setTagline(registerRequest.getTagline());

        User saved = userRepository.save(user);
        return mapToUserDto(saved);
    }

    @Override
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getTagline() != null) {
            user.setTagline(request.getTagline());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password does not match.");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        User updated = userRepository.save(user);
        return mapToUserDto(updated);
    }

    public UserDto mapToUserDto(User user) {
        if (user == null) return null;
        long articlesCount = articleRepository.countByUser(user);
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .tagline(user.getTagline())
                .createdAt(user.getCreatedAt())
                .articlesCount(articlesCount)
                .build();
    }
}
