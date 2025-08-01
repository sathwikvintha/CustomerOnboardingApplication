package com.customeronboarding.admin.service;

public interface OtpService {
    void generateAndSendOtp(String email);
    void sendOtp(String email);
    boolean verifyOtp(String email, String otp);
}