package com.example.user_service.model.User.Friend;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter
public class SendFriendRequest {
    private UUID receiverId;
}
