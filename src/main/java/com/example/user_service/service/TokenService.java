package com.example.user_service.service;

import com.example.user_service.dto.auth.TokenResponse;
import com.example.user_service.enity.User;

public interface TokenService {

    String generateAccessToken(User user);

    String generateRefreshToken(String userId, String macAddress);

    TokenResponse generateTokenByRefreshToken(String userId, String macAddress);

    void invalidateTokens(String accessToken, String refreshToken);
}
