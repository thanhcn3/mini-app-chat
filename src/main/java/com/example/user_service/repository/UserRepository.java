package com.example.user_service.repository;

import com.example.user_service.enity.User;
import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository  extends JpaRepository<User, UUID> {
    User findByUsername(String username);

}
