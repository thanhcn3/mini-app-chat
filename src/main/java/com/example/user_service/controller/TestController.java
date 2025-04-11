package com.example.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
    private final RedisTemplate<String, String> tokenRedisTemplate;

    @GetMapping("/redis")
    public ResponseEntity<String> testRedis() {
        try {
            String key = "test:key";
            String value = "Hello Redis!";
            
            // Set value
            tokenRedisTemplate.opsForValue().set(key, value);
            
            // Get value
            String retrieved = tokenRedisTemplate.opsForValue().get(key);
            
            // Clean up
            tokenRedisTemplate.delete(key);
            
            return ResponseEntity.ok("Redis test successful! Retrieved value: " + retrieved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Redis test failed: " + e.getMessage());
        }
    }

    @GetMapping("/redis/all")
    public ResponseEntity<Map<String, String>> getAllRedisKeys() {
        try {
            Map<String, String> allValues = new HashMap<>();
            
            // Get all keys
            Set<String> keys = tokenRedisTemplate.keys("*");
            
            if (keys != null) {
                for (String key : keys) {
                    String value = tokenRedisTemplate.opsForValue().get(key);
                    allValues.put(key, value);
                }
            }
            
            return ResponseEntity.ok(allValues);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/redis/jwt")
    public ResponseEntity<Map<String, String>> getAllJwtTokens() {
        try {
            Map<String, String> jwtTokens = new HashMap<>();
            
            // Get all JWT keys (using the prefix "JWT:")
            Set<String> keys = tokenRedisTemplate.keys("JWT:*");
            
            if (keys != null) {
                for (String key : keys) {
                    String value = tokenRedisTemplate.opsForValue().get(key);
                    jwtTokens.put(key, value);
                }
            }
            
            return ResponseEntity.ok(jwtTokens);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/redis/clear")
    public ResponseEntity<String> clearAllRedis() {
        try {
            Set<String> keys = tokenRedisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                tokenRedisTemplate.delete(keys);
            }
            return ResponseEntity.ok("All Redis keys have been cleared");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to clear Redis: " + e.getMessage());
        }
    }
}
