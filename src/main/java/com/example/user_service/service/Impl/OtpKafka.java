package com.example.user_service.service.Impl;


import com.example.user_service.enity.User;
import com.example.user_service.exception.AppException;
import com.example.user_service.exception.ErrorCode;
import com.example.user_service.model.otp.OtpMessage;
import com.example.user_service.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpKafka {


    private final EmailService emailService;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendOtp(OtpMessage request) throws JsonProcessingException {
        if(request.getEmail() == null || request.getEmail().isEmpty()){
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        User user = userRepository.findByEmail(request.getEmail());
        if (user != null && user.getStatus().equals("ACTIVE")) {
            ObjectMapper objectMapper = new ObjectMapper();
            OtpMessage otpMessage = new OtpMessage();
            otpMessage.setEmail(request.getEmail());
            String jsonMessage = objectMapper.writeValueAsString(otpMessage);
            kafkaTemplate.send("otp-topic", jsonMessage);
            log.info("Sent message: {}", jsonMessage);
        }
        else {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "otp-topic", groupId = "otp-service")
    public void listen(String messageJson) throws JsonProcessingException {
        log.info("Received message: {}", messageJson);
        ObjectMapper objectMapper = new ObjectMapper();
        OtpMessage otpMessage = objectMapper.readValue(messageJson, OtpMessage.class);

        emailService.sendOtpEmail(otpMessage.getEmail());
    }



}
