package com.example.user_service.service.Impl;

import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.enity.User;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.ErrorCode;
import com.example.user_service.model.User.Login.LoginRequest;
import com.example.user_service.model.User.Register.RegisterRequest;
import com.example.user_service.model.auth.TokenResponse;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
@Service
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtil;


    @Override
    public TokenResponse login(LoginRequest request) {
        User user = validateUser(request);
        String accessToken = jwtUtil.generateTokenAccess(user);
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()), request.getMacAddress());
        return new TokenResponse(
            accessToken,
            3600000, // 1 hour in milliseconds
            refreshToken,
            604800000 // 7 days in milliseconds
        );
    }

    @Override
    public String register(RegisterRequest request) {
       checkUserExistence(request);
       User user = createUser(request);
       userRepository.save(user);
       return "Register successful";
    }

    @Override
    public TokenResponse refreshToken(String refreshToken, String macAddress) {
        TokenResponse tokenResponse = jwtUtil.genTokenByRefreshToken(refreshToken, macAddress);
        return tokenResponse;
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        jwtUtil.invalidateTokens(accessToken, refreshToken);
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

    private User validateUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASS_NOT_EXISTED);
        }
        if(!user.getStatus().equals("ACTIVE")){
            throw new AppException(ErrorCode.VERIFY_MAIL);
        }
        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void checkUserExistence(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodePassword(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole("USER");
        user.setStatus("ACTIVE");
        return user;
    }
}
