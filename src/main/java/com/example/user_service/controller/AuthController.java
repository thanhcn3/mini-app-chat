package com.example.user_service.controller;


import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.exception.GlobalExceptionHandler;
import com.example.user_service.model.ApiResponse;
import com.example.user_service.model.User.Login.LoginRequest;
import com.example.user_service.model.User.Register.RegisterRequest;
import com.example.user_service.model.auth.TokenResponse;
import com.example.user_service.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    JwtUtils jwtUtils;
    AuthService authService;

    @PostMapping(value = "/login", consumes = "application/json")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);
        return ApiResponse.success(tokens);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ApiResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        String tokens = authService.register(registerRequest);
        return ApiResponse.success(tokens);
    }

    @PostMapping(value = "/refresh" ,consumes = "application/json")
    public ApiResponse<TokenResponse> refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken,
            @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.refreshToken(refreshToken, request.getMacAddress());
        return ApiResponse.success(tokens);
    }

    @PostMapping(value = "/logout"  ,consumes = "application/json")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken
    ) {
        String accessToken = authHeader.substring(7);
        authService.logout(accessToken, refreshToken);
        return ApiResponse.success(null);
    }

    @GetMapping("/getId")
    public ApiResponse<String> getId(@RequestHeader("Authorization") String authHeader ) {
        String token = authHeader.substring(7);
        String userId = jwtUtils.extractUserId(token);
        String role = jwtUtils.extractUserRole(token);
        String message = "User ID: " + userId + ", Role: " + role;
        return ApiResponse.success(message);

    }
}


