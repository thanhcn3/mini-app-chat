package com.example.user_service.dto.User.Friend.RequestFriend;

import lombok.Data;

import java.util.List;

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
