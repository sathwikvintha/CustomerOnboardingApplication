package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.OtpData;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final JavaMailSender mailSender;

    // In-memory store: email -> OTP data
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    @Override
    public void generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        // Store OTP in memory
        otpStore.put(email, new OtpData(otp, expiryTime));

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
        mailSender.send(message);

    }

    @Override
    public void sendOtp(String email) {
        generateAndSendOtp(email);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpStore.get(email);
        if (otpData == null) {
            System.out.println("No OTP found for email: " + email);
            return false;
        }

        if (otpData.getExpiry().isBefore(LocalDateTime.now())) {
            System.out.println("OTP expired for email: " + email);
            return false;
        }

        boolean isValid = otpData.getOtp().equals(otp);
        if (!isValid) {
            System.out.println("Invalid OTP provided for email: " + email);
            return false;
        }

        otpStore.remove(email); // OTP used, remove from store
        return true;
    }

}
