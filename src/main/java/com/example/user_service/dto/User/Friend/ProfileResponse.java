package com.example.user_service.dto.User.Friend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class ProfileResponse {
    private UUID id;
//    private String username;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String status;
    private String gender;
    private String avatar;
    private String background;
    private String description;
    private Timestamp birthday;
    private Timestamp createdAt;

}
