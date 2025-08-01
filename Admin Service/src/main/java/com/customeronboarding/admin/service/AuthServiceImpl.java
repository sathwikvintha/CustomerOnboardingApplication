package com.customeronboarding.admin.service;

import com.customeronboarding.admin.config.JwtUtils;
import com.customeronboarding.admin.dto.AuthResponseDTO;
import com.customeronboarding.admin.entity.AdminUser;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.repository.AdminUserRepository;
import com.customeronboarding.admin.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // ✅ Step 1: Validate credentials during login (used in /login)
    @Override
    public void validateCredentials(String username, String password) {
        // Try AdminUser first
        AdminUser admin = userRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials");
            }
            return;
        }

        // Try Customer
        Customer customer = customerRepository.findByEmail(username).orElse(null);
        if (customer != null) {
            if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
                throw new RuntimeException("Invalid credentials");
            }
            return;
        }

        throw new RuntimeException("User not found");
    }

    // ✅ Step 2: Generate JWT after OTP is verified (used in /verify-otp)
    @Override
    public AuthResponseDTO generateToken(String username) {
        // Try AdminUser
        AdminUser admin = userRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            String token = jwtUtils.generateToken(admin.getUsername());
            return new AuthResponseDTO(token);
        }

        // Try Customer
        Customer customer = customerRepository.findByEmail(username).orElse(null);
        if (customer != null) {
            String token = jwtUtils.generateToken(customer.getEmail());
            return new AuthResponseDTO(token);
        }

        throw new RuntimeException("User not found");
    }
}
