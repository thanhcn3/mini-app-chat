package com.example.user_service.model.User.Register;


import lombok.Data;

@Data
public class Request {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
}
