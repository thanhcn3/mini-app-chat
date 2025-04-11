package com.example.user_service.model.User.Login;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
