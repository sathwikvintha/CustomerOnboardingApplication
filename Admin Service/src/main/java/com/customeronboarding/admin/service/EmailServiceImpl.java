package com.customeronboarding.admin.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendKycStatusUpdate(String to, String status, String customerName) {
        Context context = new Context();
        context.setVariable("name", customerName);
        context.setVariable("status", status);
        sendEmail(to, "KYC Status Update", "kyc-status", context);
    }

    @Override
    public void sendAccountCreated(String to, String customerName) {
        Context context = new Context();
        context.setVariable("name", customerName);
        sendEmail(to, "Your Account Has Been Created", "account-created", context);
    }

    @Override
    public void sendPasswordReset(String to, String resetLink) {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        sendEmail(to, "Password Reset Request", "password-reset", context);
    }

    // ✅ New OTP method
    @Override
    public void sendOtp(String to, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        sendEmail(to, "Your OTP for Password Reset", "otp-email", context); // 'otp-email.html' should exist
    }

    private void sendEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
