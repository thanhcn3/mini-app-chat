package com.example.user_service.service.Impl;

import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.dto.auth.TokenResponse;
import com.example.user_service.enity.User;
import com.example.user_service.service.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenServiceImpl implements TokenService {

    JwtUtils jwtUtils;

    @Override
    public String generateAccessToken(User user) {
        return jwtUtils.generateTokenAccess(user);
    }

    @Override
    public String generateRefreshToken(String userId, String macAddress) {
        return jwtUtils.generateRefreshToken(userId, macAddress);
    }

    @Override
    public TokenResponse generateTokenByRefreshToken(String userId, String macAddress) {
        return jwtUtils.genTokenByRefreshToken(userId, macAddress);
    }


    @Override
    public void invalidateTokens(String accessToken, String refreshToken) {
        jwtUtils.invalidateTokens(accessToken, refreshToken);
    }
}
