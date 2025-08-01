package com.customeronboarding.customer.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private Long customerId;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String dob;
    private String address;
    private String panNumber;
    private String aadhaarNumber;
    private String createdAt;
}
