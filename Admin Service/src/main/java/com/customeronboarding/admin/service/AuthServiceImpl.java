package com.customeronboarding.admin.service;

import com.customeronboarding.admin.util.OtpUtil;
import com.customeronboarding.admin.config.JwtUtils;
import com.customeronboarding.admin.dto.AuthResponseDTO;
import com.customeronboarding.admin.dto.OtpData;
import com.customeronboarding.admin.dto.ResetPasswordRequestDTO;
import com.customeronboarding.admin.entity.AdminUser;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.repository.AdminUserRepository;
import com.customeronboarding.admin.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository adminuserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpUtil otpUtil;

    // In-memory OTP store
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    @Override
    public void validateCredentials(String username, String password) {
        AdminUser admin = adminuserRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials");
            }
            return;
        }

        Customer customer = customerRepository.findByEmail(username).orElse(null);
        if (customer != null) {
            Long userId = customer.getUserId();
            AdminUser linkedUser = adminuserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User credentials not found"));
            if (!passwordEncoder.matches(password, linkedUser.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials");
            }
            return;
        }

        throw new RuntimeException("User not found");
    }

    @Override
    public AuthResponseDTO generateToken(String username) {
        AdminUser admin = adminuserRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            String token = jwtUtils.generateToken(admin.getUsername());
            return new AuthResponseDTO(token);
        }

        Customer customer = customerRepository.findByEmail(username).orElse(null);
        if (customer != null) {
            return new AuthResponseDTO(jwtUtils.generateToken(customer.getEmail()));
        }

        throw new RuntimeException("User not found");
    }

    @Override
    public void sendOtpForPasswordReset(String username) {
        AdminUser user = adminuserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));

        Long userId = user.getId();

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer record not found"));

        String email = customer.getEmail();

        String otp = otpUtil.generateOtp();

        OtpData otpData = new OtpData(otp, LocalDateTime.now().plusMinutes(5)); // 5 min expiry
        otpStore.put(email, otpData);  // ✅ store using email

        emailService.sendOtp(email, otp);
    }

    @Override
    public String resetPasswordWithOtp(ResetPasswordRequestDTO request) {
        String username = request.getUsername();
        String otpInput = request.getOtp();
        String newPassword = request.getNewPassword();

        // Get user to lookup actual email (used for storing OTP)
        AdminUser user = adminuserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Long userId = user.getId();
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        String email = customer.getEmail();

        OtpData otpData = otpStore.get(email);

        if (otpData == null || otpData.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired or not found");
        }

        if (!otpData.getOtp().equals(otpInput)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Reset password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        adminuserRepository.save(user);
        otpStore.remove(email);

        return "Password reset successful";
    }
}
