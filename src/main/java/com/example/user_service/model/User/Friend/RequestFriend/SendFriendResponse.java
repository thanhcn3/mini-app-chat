package com.example.user_service.model.User.Friend.RequestFriend;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
public class SendFriendResponse {
    private Long count;
    private List<ListUserResponse> list;

    public SendFriendResponse(Long count, List<ListUserResponse> list) {
        this.count = count;
        this.list = list;
    }

    public SendFriendResponse() {
    }
}
