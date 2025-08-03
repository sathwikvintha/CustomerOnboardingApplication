package com.customeronboarding.customer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerChangePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;

}
