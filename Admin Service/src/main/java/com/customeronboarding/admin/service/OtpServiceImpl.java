package com.customeronboarding.admin.service;

import com.customeronboarding.admin.entity.OtpVerification;
import com.customeronboarding.admin.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepo;
    private final JavaMailSender mailSender;

    @Override
    public String generateAndSendOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        Optional<OtpVerification> existingOtpOpt = otpRepo.findByEmail(email);

        OtpVerification otpRecord = existingOtpOpt.map(existing -> {
            existing.setOtp(otp);
            existing.setExpiryTime(expiryTime);
            existing.setVerified(false);
            return existing;
        }).orElse(OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiryTime(expiryTime)
                .verified(false)
                .build());

        otpRepo.save(otpRecord);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
        mailSender.send(message);

        return "OTP sent successfully to " + email;
    }

    @Override
    public void sendOtp(String email) {
        generateAndSendOtp(email);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        return otpRepo.findByEmailAndOtp(email, otp)
                .filter(o -> !o.isVerified())
                .filter(o -> o.getExpiryTime().isAfter(LocalDateTime.now()))
                .map(entity -> {
                    entity.setVerified(true);
                    otpRepo.save(entity);
                    return true;
                }).orElse(false);
    }
}
