package com.customeronboarding.admin.controller;

import com.customeronboarding.admin.dto.AuthRequestDTO;
import com.customeronboarding.admin.dto.AuthResponseDTO;
import com.customeronboarding.admin.dto.OtpVerifyRequestDTO;
import com.customeronboarding.admin.entity.AdminUser;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.repository.AdminUserRepository;
import com.customeronboarding.admin.repository.CustomerRepository;
import com.customeronboarding.admin.service.AuthService;
import com.customeronboarding.admin.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final AdminUserRepository adminUserRepository;
    private final CustomerRepository customerRepository;

    // STEP 1: Validate credentials and send OTP to customer email
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO request) {
        // 1. Validate credentials
        authService.validateCredentials(request.getUsername(), request.getPassword());

        // 2. Get AdminUser from username
        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // 3. Get Customer email from CustomerRepository using user ID
        Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer email not found for user ID: " + user.getId());
        }

        String email = customerOpt.get().getEmail();

        // 4. Send OTP to email
        otpService.sendOtp(email);

        return ResponseEntity.ok(email);
    }

    // STEP 2: Validate OTP and return JWT token
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        AuthResponseDTO response = authService.generateToken(request.getEmail());
        return ResponseEntity.ok(response);
    }
}
