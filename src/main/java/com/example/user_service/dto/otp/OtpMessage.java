package com.example.user_service.dto.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpMessage {
    private String email;
    private String otp;

    public OtpMessage(String email) {
    }
}
