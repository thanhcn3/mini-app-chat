package com.example.user_service.service.Impl;

import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.domain.user.UserDomainService;
import com.example.user_service.enity.User;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.ErrorCode;
import com.example.user_service.dto.User.Login.LoginRequest;
import com.example.user_service.dto.User.Register.RegisterRequest;
import com.example.user_service.dto.auth.TokenResponse;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthServiceImpl implements AuthService {
     UserRepository userRepository;
     UserDomainService userDomainService;
     TokenServiceImpl tokenService;

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userDomainService.validateUser(request);
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(String.valueOf(user.getId()), request.getMacAddress());
        return new TokenResponse(accessToken, 3600000, refreshToken, 604800000);
    }

    @Override
    public String register(RegisterRequest request) {
       userDomainService.checkUserExistence(request);
       User user = userDomainService.createUser(request);
       userRepository.save(user);
       return "Register successful";
    }

    @Override
    public TokenResponse refreshToken(String refreshToken, String macAddress) {
        TokenResponse response = tokenService.generateTokenByRefreshToken(refreshToken, macAddress);
        return response;
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        tokenService.invalidateTokens(accessToken, refreshToken);
    }

    @Override
    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String userId = "";
        userId = userRepository.findByUsername(username).getId().toString();
        if(userId != null){
            return userId;
        }
        throw new AppException(ErrorCode.USER_NOT_EXISTED);
    }



}
