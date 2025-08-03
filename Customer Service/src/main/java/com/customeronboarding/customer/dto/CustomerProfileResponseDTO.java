package com.customeronboarding.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileResponseDTO {
    private Long customerId;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String address;
    private String panNumber;
    private String aadhaarNumber;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
