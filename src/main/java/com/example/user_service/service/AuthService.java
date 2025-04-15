package com.example.user_service.service;

import com.example.user_service.dto.User.Login.LoginRequest;
import com.example.user_service.dto.User.Register.RegisterRequest;
import com.example.user_service.dto.auth.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    String register(RegisterRequest registerRequest);

    TokenResponse refreshToken(String refreshToken, String macAddress);

    void logout(String accessToken, String refreshToken);

    String getUserId();
}
