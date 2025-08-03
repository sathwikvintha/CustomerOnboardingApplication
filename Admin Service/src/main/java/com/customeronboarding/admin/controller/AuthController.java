package com.customeronboarding.admin.controller;

import com.customeronboarding.admin.feign.CustomerActivityClient;
import com.customeronboarding.admin.util.TOTPUtil;
import com.customeronboarding.admin.config.JwtUtils;
import com.customeronboarding.admin.dto.*;
import com.customeronboarding.admin.entity.AdminUser;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.repository.AdminUserRepository;
import com.customeronboarding.admin.repository.CustomerRepository;
import com.customeronboarding.admin.service.AuthService;
import com.customeronboarding.admin.service.OtpService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final AdminUserRepository adminUserRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtils jwtUtils;
    private final CustomerActivityClient customerActivityClient;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO request) {
        authService.validateCredentials(request.getUsername(), request.getPassword());
        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        String role = user.getRole();
        String description = role.equalsIgnoreCase("CUSTOMER") ? "Customer logged in" : "Admin logged in";
        customerActivityClient.logActivity(
                user.getId(),
                "LOGIN",
                description
        );
        Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer email not found for user ID: " + user.getId());
        }
        String email = customerOpt.get().getEmail();
        otpService.sendOtp(email);
        return ResponseEntity.ok(email);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        Optional<Customer> customerOpt = customerRepository.findByEmail(request.getEmail());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customerActivityClient.logActivity(customer.getCustomerId(), "VERIFY_OTP", "OTP verified successfully");
        }
        AuthResponseDTO response = authService.generateToken(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/request-reset")
    public ResponseEntity<String> requestReset(@RequestBody OtpRequestDTO request) {
        authService.sendOtpForPasswordReset(request.getUsername());
        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
            customerOpt.ifPresent(customer ->
                    customerActivityClient.logActivity(customer.getCustomerId(), "REQUEST_RESET", "Requested OTP for password reset")
            );
        }
        return ResponseEntity.ok("OTP sent to your registered email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        String msg = authService.resetPasswordWithOtp(request);
        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
            customerOpt.ifPresent(customer ->
                    customerActivityClient.logActivity(customer.getCustomerId(), "RESET_PASSWORD", "Password reset successfully")
            );
        }

        return ResponseEntity.ok(msg);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtUtils.resolveToken(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("Token not found in request header");
        }
        String username = jwtUtils.extractUsernameFromToken(token);

        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
            customerOpt.ifPresent(customer ->
                    customerActivityClient.logActivity(customer.getCustomerId(), "LOGOUT", "User logged out")
            );
        }
        return ResponseEntity.ok("Logout successful. Please discard token on client side.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenRequestDTO request) {
        try {
            String username = jwtUtils.extractUsernameFromToken(request.getRefreshToken());
            String newAccessToken = jwtUtils.generateToken(username); // reuse existing logic
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
        }
    }

    @GetMapping("/setup-2fa")
    public ResponseEntity<Map<String, String>> setup2FA(@RequestParam String username) throws QrGenerationException {
        String secret = TOTPUtil.generateSecretKey();
        String qrCodeUrl = TOTPUtil.getQRImage(secret, username, "CustomerOnboardingApp");

        adminUserRepository.findByUsername(username).flatMap(user -> customerRepository.findByUserId(user.getId())).ifPresent(customer ->
                customerActivityClient.logActivity(
                        customer.getCustomerId(),
                        "SETUP_2FA",
                        "2FA setup initiated"
                ));

        Map<String, String> response = new HashMap<>();
        response.put("secret", secret);
        response.put("qrCodeBase64", qrCodeUrl);
        return ResponseEntity.ok(response);
    }
}
