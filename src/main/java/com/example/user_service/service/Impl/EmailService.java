package com.example.user_service.service.Impl;
import com.example.user_service.model.otp.OtpMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final long OTP_EXPIRATION = 5 * 60 * 1000; // 5 min
    private static final String REDIS_OTP_PREFIX = "OTP:";

    private final Random random = new Random();

    public void sendOtpEmail(String to) {
        String otp = String.format("%06d", random.nextInt(1000000)); // Tạo OTP 6 số
        String key = REDIS_OTP_PREFIX + to;
        redisTemplate.opsForValue().set(key, otp,  OTP_EXPIRATION, TimeUnit.MILLISECONDS);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        javaMailSender.send(message);
    }

    public boolean verifyOtp(OtpMessage request) {
        String key = REDIS_OTP_PREFIX + request.getEmail();
        String otp = request.getOtp();
        String storedOtp = redisTemplate.opsForValue().get(key);
        boolean isValid = otp.equals(storedOtp);
        if(isValid){
            redisTemplate.delete(key);
        }
        return isValid;
    }


}