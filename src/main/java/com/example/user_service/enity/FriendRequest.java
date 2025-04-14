package com.example.user_service.enity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    private String status; // PENDING, ACCEPTED, REJECTED
    private Timestamp sentAt;

    public FriendRequest(UUID id, User sender, User receiver, String status, Timestamp sentAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.sentAt = sentAt;
    }
}
