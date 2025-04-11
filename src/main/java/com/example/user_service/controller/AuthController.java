package com.example.user_service.controller;


import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.model.ApiResponse;
import com.example.user_service.model.User.Login.LoginRequest;
import com.example.user_service.model.User.Register.Request;
import com.example.user_service.model.auth.TokenResponse;
import com.example.user_service.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

     JwtUtils jwtUtils;
     AuthService authService;

    @PostMapping(value = "/login", consumes = "application/json")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse tokens = authService.login(request);
            return new ApiResponse<>("Success", "Login successful", tokens);
        } catch (Exception e) {
            return new ApiResponse<>("Error", e.getMessage(), null);
        }
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ApiResponse<String> register(@RequestBody Request request) {
        try {
            String tokens = authService.register(request);
            return new ApiResponse<>("Success", "Register successful", tokens);
        } catch (Exception e) {
            return new ApiResponse<>("Error", e.getMessage(), null);
        }
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            TokenResponse tokens = jwtUtils.refreshToken(refreshToken);
            return new ApiResponse<>("Success", "Token refreshed successfully", tokens);
        } catch (Exception e) {
            return new ApiResponse<>("Error", e.getMessage(), null);
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken
    ) {
        try {
            String accessToken = authHeader.substring(7);
            jwtUtils.invalidateTokens(accessToken, refreshToken);
            return new ApiResponse<>("Success", "Logged out successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>("Error", e.getMessage(), null);
        }
    }

    @GetMapping("/getId")
    public ApiResponse<String> getId(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.substring(7).trim();
            String userId = jwtUtils.extractUserId(token);
            String role = jwtUtils.extractUserRole(token);
            String message = "User ID: " + userId + ", Role: " + role;
            return new ApiResponse<>("Success", "Successfully retrieved user ID", message);
        } catch (Exception e) {
            return new ApiResponse<>("Error", e.getMessage(), null);
        }
    }
}
