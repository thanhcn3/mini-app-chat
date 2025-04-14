package com.example.user_service.controller;


import com.example.user_service.enity.FriendRequest;
import com.example.user_service.model.ApiResponse;
import com.example.user_service.model.User.Friend.RequestFriend.SendFriendResponse;
import com.example.user_service.model.User.Friend.RequestFriend.UserRequest;
import com.example.user_service.model.User.Friend.SendFriendRequest;
import com.example.user_service.service.FriendService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {

    FriendService friendService;

    @PostMapping(value = "/send-request",consumes = "application/json")
    public ApiResponse<String> sendFriendRequest( @RequestBody SendFriendRequest request) {
        String result = friendService.sendFriendRequest(request);
        return ApiResponse.success(result);
    }

    @PostMapping(value = "/list-request-from-user",consumes = "application/json")
    public ApiResponse<List<SendFriendResponse>> listRequestFromUser(@RequestBody UserRequest request) {
        return ApiResponse.success(friendService.getIncomingRequests(request));
    }

 @PostMapping(value = "/list-request-to-user",consumes = "application/json")
    public ApiResponse<List<SendFriendResponse>> listRequestToUser(@RequestBody UserRequest request) {
        return ApiResponse.success(friendService.getSentRequests(request));
    }

}
