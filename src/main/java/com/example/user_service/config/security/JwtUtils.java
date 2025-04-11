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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Qualifier("tokenRedisTemplate")
    private final RedisTemplate<String, String> tokenRedisTemplate;

    @Value("${jwt.secret}")
    private String rawSecretKey;
    private Key SIGNING_KEY;

    @PostConstruct
    public void init() {
        this.SIGNING_KEY = Keys.hmacShaKeyFor(rawSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    private final UserRepository userRepository;
    private static final String REDIS_JWT_PREFIX = "accessToken:";
    private static final String REDIS_REFRESH_PREFIX = "refreshToken:";
    private static final long JWT_EXPIRATION = 2 * 24 * 60 * 60 * 1000; // 2 days
    private static final long REFRESH_EXPIRATION = 5 * 24 * 60 * 60 * 1000; // 5 days


    public String generateTokenAccess(User user) {
        String accessToken = generateAccessToken(user, JWT_EXPIRATION);
        String redisKey = REDIS_JWT_PREFIX + accessToken;
        tokenRedisTemplate.opsForValue().set(redisKey, String.valueOf(user.getId()), JWT_EXPIRATION, TimeUnit.MILLISECONDS);
        return generateAccessToken(user, JWT_EXPIRATION);
    }
    public String generateRefreshToken(String userId, String macAddress) {
        String refreshToken = generateTokenRefresh(userId, macAddress);
        String redisKey = REDIS_REFRESH_PREFIX + refreshToken;
        tokenRedisTemplate.opsForValue().set(redisKey, userId, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);
        return refreshToken;
    }
    private String generateAccessToken(User user, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateTokenRefresh(String userId, String macAddress) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("MAC", macAddress)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 ngày
                .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
                .compact();

    }


    public TokenResponse genTokenByRefreshToken(String refreshToken, String macAddress) {

        String token = refreshToken.substring(7).trim();
        String redisKey = REDIS_REFRESH_PREFIX + token;
        String macAddressToken = extractMacAddress(refreshToken);
        String userId = tokenRedisTemplate.opsForValue().get(redisKey);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user == null || !macAddress.equals(macAddressToken)){
            throw new RuntimeException("Invalid refresh token");
        }
        //Delete old refresh token
        tokenRedisTemplate.delete(redisKey);

        String newAccessToken = generateTokenAccess(user);
        String newRefreshToken = generateRefreshToken(userId, macAddress);

        return new TokenResponse(
            newAccessToken,
            JWT_EXPIRATION,
            newRefreshToken,
            REFRESH_EXPIRATION
        );
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

    public void invalidateTokens(String accessToken, String refreshToken) {
        tokenRedisTemplate.delete(REDIS_JWT_PREFIX + accessToken);
        tokenRedisTemplate.delete(REDIS_REFRESH_PREFIX + refreshToken);
    }

    public boolean isValidAccessToken(String token, String userId) {
        String redisKey = REDIS_JWT_PREFIX + token;
        String storedId = tokenRedisTemplate.opsForValue().get(redisKey);
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

    public String extractUserRole(String token) {
        String accessToken = token.substring(7).trim();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        return claims.get("role", String.class);
    }

    public String extractMacAddress(String token) {
        String refreshToken = token.substring(7).trim();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return claims.get("MAC", String.class);
    }

    public String getUserIdFromRefreshToken(String refreshToken) {
        return tokenRedisTemplate.opsForValue().get(REDIS_REFRESH_PREFIX + refreshToken);
    }


}
