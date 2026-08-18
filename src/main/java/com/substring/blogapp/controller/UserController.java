package com.substring.blogapp.controller;

import com.substring.blogapp.dto.UserDto;
import com.substring.blogapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Public user profile and author lookup endpoints")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get user public profile by email")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String email) {
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }
}
