package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.AuthRequestDTO;
import com.customeronboarding.admin.dto.AuthResponseDTO;
import com.customeronboarding.admin.dto.ResetPasswordRequestDTO;

public interface AuthService {
    void validateCredentials(String email, String password);
    AuthResponseDTO generateToken(String email);
    void sendOtpForPasswordReset(String username);
    String resetPasswordWithOtp(ResetPasswordRequestDTO request);

}
