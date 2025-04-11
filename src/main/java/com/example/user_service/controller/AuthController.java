package com.example.user_service.controller;


import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.GlobalExceptionHandler;
import com.example.user_service.model.ApiResponse;
import com.example.user_service.model.User.Login.LoginRequest;
import com.example.user_service.model.User.Register.Request;
import com.example.user_service.model.auth.TokenResponse;
import com.example.user_service.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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
            return ApiResponse.success(tokens);
        } catch (Exception e) {
            throw new GlobalExceptionHandler.BusinessException(e.getMessage());
        }
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ApiResponse<String> register(@RequestBody Request request) {
        try {
            String tokens = authService.register(request);
            return ApiResponse.success(tokens);
        } catch (Exception e) {
            throw new GlobalExceptionHandler.BusinessException(e.getMessage());
        }
    }

    @PostMapping(value = "/refresh" ,consumes = "application/json")
    public ApiResponse<TokenResponse> refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken,
            @RequestBody LoginRequest request) {
        try {
            TokenResponse tokens = jwtUtils.genTokenByRefreshToken(refreshToken, request.getMacAddress());
            return ApiResponse.success(tokens);
        } catch (Exception e) {
            throw new GlobalExceptionHandler.BusinessException(e.getMessage());
        }
    }

    @PostMapping(value = "/logout"  ,consumes = "application/json")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken
    ) {
        try {
            String accessToken = authHeader.substring(7);
            jwtUtils.invalidateTokens(accessToken, refreshToken);
            return ApiResponse.success(null);
        } catch (Exception e) {
            throw new GlobalExceptionHandler.BusinessException(e.getMessage());
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
            return ApiResponse.success(message);
        } catch (Exception e) {
            throw new GlobalExceptionHandler.BusinessException(e.getMessage());
        }
    }
}
