package com.example.user_service.service;

import com.example.user_service.dto.User.Friend.RequestFriend.SendFriendResponse;
import com.example.user_service.dto.User.Friend.SendFriendRequest;

import java.util.UUID;

public interface FriendService {
    //Send request add friend
    String sendFriendRequest(SendFriendRequest request);
    //Accept request add friend
    String acceptRequest(UUID requestId);
    //List send to user pending
    SendFriendResponse getIncomingRequests();
    //List send from user pending
    SendFriendResponse getSentRequests();
}
