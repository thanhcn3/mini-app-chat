package com.example.user_service.controller;

import com.example.user_service.exception.GlobalExceptionHandler;
import com.example.user_service.model.ApiResponse;
import com.example.user_service.model.otp.OtpMessage;
import com.example.user_service.service.Impl.EmailService;
import com.example.user_service.service.Impl.OtpKafka;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpController {

    OtpKafka otpKafka;
    EmailService mailService;

    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(@RequestBody OtpMessage requset) {
        try {
            otpKafka.sendOtp(requset);
            ApiResponse.success("OTP sent successfully");
        } catch (Exception e) {
            throw new GlobalExceptionHandler.BusinessException(e.getMessage());
        }
        return null;
    }
    @PostMapping("/check-otp")
    public ApiResponse<Boolean> checkOtp(@RequestBody OtpMessage requset) {
      try{
          return ApiResponse.success(mailService.verifyOtp(requset));
      }catch (Exception e){
          throw new GlobalExceptionHandler.BusinessException("Mail not verified");
      }

    }



}
