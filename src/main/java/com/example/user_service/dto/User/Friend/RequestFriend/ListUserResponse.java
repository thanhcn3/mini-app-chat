package com.example.user_service.dto.User.Friend.RequestFriend;

import lombok.Data;

import java.util.UUID;


@Data
public class ListUserResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String avatar;

    public ListUserResponse() {
    }

    public ListUserResponse(UUID id, UUID userId, String name, String avatar) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.avatar = avatar;
    }
}
