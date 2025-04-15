package com.example.user_service.domain.user;

import com.example.user_service.dto.User.Login.LoginRequest;
import com.example.user_service.dto.User.Register.RegisterRequest;
import com.example.user_service.enity.User;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.ErrorCode;
import com.example.user_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDomainService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;


    public User validateUser(LoginRequest request) {
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

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void checkUserExistence(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }
    public User createUser(RegisterRequest request) {
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
