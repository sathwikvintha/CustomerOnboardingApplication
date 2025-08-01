package com.customeronboarding.admin.service;

public interface EmailService {
    void sendKycStatusUpdate(String to, String status, String customerName);
    void sendAccountCreated(String to, String customerName);
    void sendPasswordReset(String to, String resetLink);
    void sendOtp(String to, String otp);

}
