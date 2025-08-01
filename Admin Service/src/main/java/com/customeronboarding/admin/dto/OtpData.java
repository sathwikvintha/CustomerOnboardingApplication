package com.customeronboarding.admin.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OtpData {
    private String otp;
    private LocalDateTime expiry;

    public OtpData(String otp, LocalDateTime expiry) {
        this.otp = otp;
        this.expiry = expiry;
    }

}
