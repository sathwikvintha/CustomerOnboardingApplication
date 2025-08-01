package com.customeronboarding.admin.dto;

import lombok.Data;

@Data
public class UserRegistrationRequestDTO {
    private String username;
    private String role;
    private String password;
}
