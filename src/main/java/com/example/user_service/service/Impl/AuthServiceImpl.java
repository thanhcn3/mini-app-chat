package com.example.user_service.service.Impl;

import com.example.user_service.config.security.JwtUtils;
import com.example.user_service.enity.User;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.ErrorCode;
import com.example.user_service.model.User.Login.LoginRequest;
import com.example.user_service.model.User.Register.Request;
import com.example.user_service.model.auth.TokenResponse;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
        User user = userRepository.findByUsername(request.getUsername());
        if (user != null) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AppException(ErrorCode.USERAPASS_NOT_EXISTED);
            }
            String accessToken = jwtUtil.generateTokenAccess(user);
            String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()), request.getMacAddress());
            return new TokenResponse(
                    accessToken,
                    3600000, // 1 hour in milliseconds
                    refreshToken,
                    604800000 // 7 days in milliseconds
            );
        } else {
            throw new RuntimeException("Invalid Username");
        }
    }

    @Override
    public String register(Request request) {
        if(userRepository.findByUsername(request.getUsername()) != null){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setAddress(request.getAddress());
        newUser.setRole("USER");
        newUser.setStatus("ACTIVE");
        userRepository.save(newUser);

       return "Register successful";
    }
}
