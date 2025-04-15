package com.example.user_service.service;

import com.example.user_service.enity.FriendRequest;
import com.example.user_service.model.User.Friend.RequestFriend.SendFriendResponse;
import com.example.user_service.model.User.Friend.RequestFriend.TestResponse;
import com.example.user_service.model.User.Friend.RequestFriend.UserRequest;
import com.example.user_service.model.User.Friend.SendFriendRequest;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    String sendFriendRequest(SendFriendRequest request);

    String acceptRequest(UUID requestId);

    //List send to user pending
    SendFriendResponse getIncomingRequests(UserRequest request);
    //List send from user pending
    SendFriendResponse getSentRequests(UserRequest request);
}
