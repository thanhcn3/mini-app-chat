package com.example.user_service.model.User.Friend.RequestFriend;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class SendFriendResponse {
    private Long count;
    private UUID id;
    private UUID userId;
    private String name;
    private String avatar;

    public SendFriendResponse(Long count, UUID id, UUID userId, String name, String avatar) {
        this.count = count;
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.avatar = avatar;
    }
}
