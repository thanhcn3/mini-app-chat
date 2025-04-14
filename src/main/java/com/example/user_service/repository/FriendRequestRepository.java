package com.example.user_service.repository;

import com.example.user_service.enity.FriendRequest;
import com.example.user_service.enity.User;
import com.example.user_service.model.User.Friend.RequestFriend.SendFriendResponse;
import com.example.user_service.until.RequestStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

//    List<SendFriendResponse> findAllByReceiverAndStatus(User receiver, RequestStatus status);

    @Query(value = "SELECT new com.example.user_service.model.User.Friend.RequestFriend.SendFriendResponse(count(fr.id),fr.id, u.id, u.name, u.avatar) " +
            "FROM FriendRequest fr" +
            " JOIN User u ON fr.receiver.id = u.id" +
            " WHERE fr.receiver.id = ?1 " +
            "AND fr.status = 'PENDING'" +
            " GROUP BY fr.id, u.id, u.name, u.avatar"
            )
    List<SendFriendResponse> findByReceiverIdAndStatus(UUID receiverId);

    @Query(value = "SELECT new com.example.user_service.model.User.Friend.RequestFriend.SendFriendResponse(count(fr.id),fr.id, u.id, u.name, u.avatar) " +
            "FROM FriendRequest fr" +
            " JOIN User u ON fr.sender.id = u.id" +
            " WHERE fr.sender.id = ?1 " +
            "AND fr.status = 'PENDING'" +
            "GROUP BY fr.id, u.id, u.name, u.avatar"
    )
    List<SendFriendResponse> findSentRequests(UUID senderId);

}
