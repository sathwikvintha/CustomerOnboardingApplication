package com.customeronboarding.admin.service;

import com.customeronboarding.admin.config.JwtUtils;
import com.customeronboarding.admin.dto.AuthRequestDTO;
import com.customeronboarding.admin.dto.AuthResponseDTO;
import com.customeronboarding.admin.entity.AdminUser;
import com.customeronboarding.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // ✅ Step 1: Validate credentials during login (used in /login)
    @Override
    public void validateCredentials(String email, String password) {
        AdminUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    // ✅ Step 2: Generate JWT after OTP is verified (used in /verify-otp)
    @Override
    public AuthResponseDTO generateToken(String email) {
        AdminUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponseDTO(token);
    }
}
