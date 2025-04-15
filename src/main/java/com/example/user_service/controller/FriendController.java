package com.example.user_service.controller;


import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.User.Friend.ProfileResponse;
import com.example.user_service.dto.User.Friend.RequestFriend.ListUserResponse;
import com.example.user_service.dto.User.Friend.RequestFriend.SendFriendResponse;
import com.example.user_service.dto.User.Friend.SendFriendRequest;
import com.example.user_service.service.FriendService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/friend-request-list-send",consumes = "application/json")
    public ApiResponse<SendFriendResponse> listRequestFromUser() {
        return ApiResponse.success(friendService.getIncomingRequests());
    }

 @PostMapping(value = "/friend-request-list",consumes = "application/json")
    public ApiResponse<SendFriendResponse>listRequestToUser() {
        return ApiResponse.success(friendService.getSentRequests());
    }

    @PostMapping(value = "/accept-request",consumes = "application/json")
    public ApiResponse<String> acceptRequest(@RequestBody SendFriendRequest request) {
        String result = friendService.acceptRequest(request.getReceiverId());
        return ApiResponse.success(result);
    }

    @PostMapping(value = "/myfriend",consumes = "application/json")
    public ApiResponse<List<ListUserResponse>> myFriend() {
        List<ListUserResponse> result = friendService.getFriends();
        return ApiResponse.success(result);
    }

    @PostMapping(value = "/profile/{userId}")
    public ApiResponse<ProfileResponse> getProfileById(@PathVariable UUID userId) {
        ProfileResponse result = friendService.getProfile(userId);
        return ApiResponse.success(result);
    }

}
