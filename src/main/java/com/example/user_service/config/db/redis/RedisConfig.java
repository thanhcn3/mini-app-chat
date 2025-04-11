package com.example.user_service.config.db.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean(name = "otpRedisTemplate")
    public RedisTemplate<String, String> otpRedisTemplate() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setDatabase(1); // DB1 cho OTP
        factory.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(name = "tokenRedisTemplate")
    public RedisTemplate<String, String> tokenRedisTemplate() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setDatabase(0); // DB0 cho token
        factory.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // Sử dụng StringRedisSerializer cho cả key và value
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//        template.setKeySerializer(stringSerializer);
//        template.setValueSerializer(stringSerializer);
//        template.setHashKeySerializer(stringSerializer);
//        template.setHashValueSerializer(stringSerializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }
}