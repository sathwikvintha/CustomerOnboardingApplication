package com.customeronboarding.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String username;
    private String otp;
    private String newPassword;
}
