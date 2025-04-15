package com.example.user_service.dto.User.Friend.RequestFriend;

import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class TestResponse {
    private Long count;
    private List<Request> test;

    public TestResponse(Long count, List<Request> test) {
        this.count = count;
        this.test = test;
    }

    public TestResponse() {
    }

    @Data
    public static class Request{
        private UUID id;
        private UUID userId;
        private String name;
        private String avatar;
        public Request(UUID id, UUID userId, String name, String avatar) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.avatar = avatar;
        }

        public Request() {
        }
    }

}
