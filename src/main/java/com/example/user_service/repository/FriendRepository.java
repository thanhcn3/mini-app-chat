package com.example.user_service.repository;

import com.example.user_service.enity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, UUID> {
}
