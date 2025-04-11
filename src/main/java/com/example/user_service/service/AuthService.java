package com.example.user_service.service;

import com.example.user_service.model.User.Login.LoginRequest;
import com.example.user_service.model.User.Register.Request;
import com.example.user_service.model.auth.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    String register(Request request);
}
