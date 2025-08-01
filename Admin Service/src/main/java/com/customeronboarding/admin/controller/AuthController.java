package com.customeronboarding.admin.controller;

import com.customeronboarding.admin.dto.AuthRequestDTO;
import com.customeronboarding.admin.dto.AuthResponseDTO;
import com.customeronboarding.admin.dto.OtpVerifyRequestDTO;
import com.customeronboarding.admin.service.AuthService;
import com.customeronboarding.admin.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    // STEP 1: Validate credentials and send OTP to email
    @PostMapping("/login")
    public String login(@RequestBody AuthRequestDTO request) {
        authService.validateCredentials(request.getUsername(), request.getPassword());
        otpService.sendOtp(request.getUsername()); // username is assumed to be the email
        return "OTP sent to email.";
    }

    // STEP 2: Validate OTP and return token
    @PostMapping("/verify-otp")
    public AuthResponseDTO verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        return authService.generateToken(request.getEmail());
    }
}
