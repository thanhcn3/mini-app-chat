package com.example.user_service.config.security;

import com.example.user_service.enity.User;
import com.example.user_service.model.auth.TokenResponse;
import com.example.user_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {
    private UserRepository userRepository;
    
    private final RedisTemplate<String, String> redisTemplate;
    private static final String rawSecretKey = "5eba11a05584e350473b6f4202a6c27c25391bf45e67b0ed03c14cb983523a81223ff1d67e31ade3de4e5ea4c5d01651a115e6c6dd6aa28dba227b647d1753be";
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(rawSecretKey.getBytes(StandardCharsets.UTF_8));
    
    private static final String REDIS_JWT_PREFIX = "JWT:";
    private static final String REDIS_REFRESH_PREFIX = "REFRESH:";
    private static final long JWT_EXPIRATION = 1 * 60 * 60 * 1000; // 1 hour
    private static final long REFRESH_EXPIRATION = 5 * 24 * 60 * 60 * 1000; // 5 days

    public String generateToken(User user) {
        return generateToken(user, JWT_EXPIRATION);
    }

    public String generateRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();
        String redisKey = REDIS_REFRESH_PREFIX + refreshToken;

        // Store refresh token in Redis
        redisTemplate.opsForValue().set(redisKey, userId, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        return  refreshToken;
    }

    private String generateToken(User user, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();

        // Store in Redis with expiration
        String redisKey = REDIS_JWT_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(user.getId()), expiration, TimeUnit.MILLISECONDS);

        return token;
    }

    public TokenResponse refreshToken(String refreshToken) {
        String redisKey = REDIS_REFRESH_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(redisKey);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new access token
        String newAccessToken = generateToken(user);

        // Extend refresh token expiration
        redisTemplate.expire(redisKey, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        return new TokenResponse(
            newAccessToken,
            JWT_EXPIRATION,
            refreshToken,
            REFRESH_EXPIRATION
        );
    }

    public boolean isValidAccessToken(String token, String userId) {
        String redisKey = REDIS_JWT_PREFIX + token;
        String storedId = redisTemplate.opsForValue().get(redisKey);
        return userId.equals(storedId) && !isTokenExpired(token);
    }
    public String extractUserId(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new RuntimeException("JWT token is expired", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public Claims extractAllClaims(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }

        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public void invalidateTokens(String accessToken, String refreshToken) {
        redisTemplate.delete(REDIS_JWT_PREFIX + accessToken);
        redisTemplate.delete(REDIS_REFRESH_PREFIX + refreshToken);
    }

    public String getUserIdFromRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(REDIS_REFRESH_PREFIX + refreshToken);
    }

    public String extractUserRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }


}
