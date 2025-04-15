package com.example.user_service.domain.time;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class TimeService {

    public long getCurrentUnixTimestamp() {
        return Instant.now().getEpochSecond(); // ví dụ: 1744701218
    }

    public Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

}
