package com.example.user_service.dto.User.Register;


import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
}
