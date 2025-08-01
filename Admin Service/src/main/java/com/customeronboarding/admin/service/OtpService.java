package com.customeronboarding.admin.service;

public interface OtpService {
    String generateAndSendOtp(String email);
    void sendOtp(String email);
    boolean verifyOtp(String email, String otp);
}