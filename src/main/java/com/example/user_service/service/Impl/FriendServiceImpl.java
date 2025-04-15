package com.example.user_service.service.Impl;

import com.example.user_service.enity.Friend;
import com.example.user_service.enity.FriendRequest;
import com.example.user_service.enity.User;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.ErrorCode;
import com.example.user_service.model.User.Friend.RequestFriend.ListUserResponse;
import com.example.user_service.model.User.Friend.RequestFriend.SendFriendResponse;
import com.example.user_service.model.User.Friend.RequestFriend.TestResponse;
import com.example.user_service.model.User.Friend.RequestFriend.UserRequest;
import com.example.user_service.model.User.Friend.SendFriendRequest;
import com.example.user_service.repository.FriendRepository;
import com.example.user_service.repository.FriendRequestRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.FriendService;
import com.example.user_service.until.RequestStatus;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class FriendServiceImpl implements FriendService {

     UserRepository userRepository;
     FriendRequestRepository friendRequestRepository;
     FriendRepository friendRepository;
    private final AuthService authService;

    @Override
    public String sendFriendRequest(SendFriendRequest request) {
        UUID userId = UUID.fromString(authService.getUserId());
        User sender = userRepository.findById(userId).orElseThrow();
        User receiver = userRepository.findById(request.getReceiverId()).orElseThrow();

        if (userId.equals(request.getReceiverId())) {
            throw new AppException(ErrorCode.CANNOT_SEND_REQUEST);
        }

        Long existing = friendRequestRepository.countPendingBetween(userId, receiver.getId());
        if (existing > 0) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_EXISTED);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setSentAt(Timestamp.valueOf(LocalDateTime.now()));
        friendRequest.setStatus("PENDING");
        friendRequestRepository.save(friendRequest);
        return "Request sent!";
    }

    @Transactional
    @Override
    public String acceptRequest(UUID requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("ACCEPTED");
        friendRequestRepository.save(request);

        friendRepository.save(new Friend(request.getSender(), request.getReceiver()));
        friendRepository.save(new Friend(request.getReceiver(), request.getSender()));
        return "Request accepted!";
    }

    @Override
    public SendFriendResponse getIncomingRequests() {
        UUID userId = UUID.fromString(authService.getUserId());
        List<ListUserResponse> list = friendRequestRepository.getFromRequests(userId);
        Long count = friendRequestRepository.getTotalPendingCount(userId);
        SendFriendResponse response = new SendFriendResponse(count, list);
        return response;
    }

    @Override
    public SendFriendResponse getSentRequests() {
        UUID userId = UUID.fromString(authService.getUserId());
        List<ListUserResponse> list = friendRequestRepository.getSentRequests(userId);
        Long count = friendRequestRepository.getTotalSendPendingCount(userId);
        SendFriendResponse response = new SendFriendResponse(count, list);
        return response;
    }
}
