package com.example.user_service.repository;

import com.example.user_service.dto.User.Friend.RequestFriend.ListUserResponse;
import com.example.user_service.enity.Friend;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, UUID> {

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f " +
            "WHERE (f.user.id = :userId1 AND f.friend.id = :userId2) " +
            "OR (f.friend.id = :userId2 AND f.user.id = :userId1)")
    boolean existsByUserId1AndUserId2(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query("SELECT new com.example.user_service.dto.User.Friend.RequestFriend.ListUserResponse(" +
            "f.id, u.id, u.name, u.avatar) " +
            "FROM Friend f " +
            "JOIN User u ON f.friend.id = u.id " +
            "WHERE f.user.id = ?1")
    List<ListUserResponse> getFriends(UUID userId);

//    boolean existsByUserId1AndUserId2(UUID userId1, UUID userId2);
}
