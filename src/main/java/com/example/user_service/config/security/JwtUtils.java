package com.example.user_service.config.security;

import com.example.user_service.model.auth.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String secretKey = "826d1d3281c52067aa518d9a08b8fbf239a8e2a15f3c21d03a734e952648bcf5";
    private static final String REDIS_JWT_PREFIX = "JWT:";
    private static final String REDIS_REFRESH_PREFIX = "REFRESH:";
    private static final long JWT_EXPIRATION = 1 * 60 * 60 * 1000; // 1 hour
    private static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateToken(String username) {
        return generateToken(username, JWT_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        String redisKey = REDIS_REFRESH_PREFIX + refreshToken;
        
        // Store refresh token in Redis
        redisTemplate.opsForValue().set(redisKey, username, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);
        
        return  refreshToken;
    }

    private String generateToken(String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Store in Redis with expiration
        String redisKey = REDIS_JWT_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, username, expiration, TimeUnit.MILLISECONDS);
        
        return token;
    }

    public TokenResponse refreshToken(String refreshToken) {
        String redisKey = REDIS_REFRESH_PREFIX + refreshToken;
        String username = redisTemplate.opsForValue().get(redisKey);
        
        if (username == null) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        
        // Generate new access token
        String newAccessToken = generateToken(username);
        
        // Extend refresh token expiration
        redisTemplate.expire(redisKey, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);
        
        return new TokenResponse(
            newAccessToken,
            refreshToken,
            JWT_EXPIRATION,
            REFRESH_EXPIRATION
        );
    }

    public void invalidateTokens(String accessToken, String refreshToken) {
        // Delete access token
        String accessKey = REDIS_JWT_PREFIX + accessToken;
        redisTemplate.delete(accessKey);
        
        // Delete refresh token
        if (refreshToken != null) {
            String refreshKey = REDIS_REFRESH_PREFIX + refreshToken;
            redisTemplate.delete(refreshKey);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, String username) {
        String redisKey = REDIS_JWT_PREFIX + token;
        String storedUsername = redisTemplate.opsForValue().get(redisKey);
        
        if (storedUsername == null) {
            return false;
        }
        
        boolean isValid = username.equals(storedUsername) && !isTokenExpired(token);
        
        // Extend token expiration if valid
        if (isValid) {
            redisTemplate.expire(redisKey, JWT_EXPIRATION, TimeUnit.MILLISECONDS);
        }
        
        return isValid;
    }

    public void logout(String token) {
        String redisKey = REDIS_JWT_PREFIX + token;
        redisTemplate.delete(redisKey);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
