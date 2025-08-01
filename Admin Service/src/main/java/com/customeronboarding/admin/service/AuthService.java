package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.AuthRequestDTO;
import com.customeronboarding.admin.dto.AuthResponseDTO;

public interface AuthService {
    void validateCredentials(String email, String password);
    AuthResponseDTO generateToken(String email);
}
