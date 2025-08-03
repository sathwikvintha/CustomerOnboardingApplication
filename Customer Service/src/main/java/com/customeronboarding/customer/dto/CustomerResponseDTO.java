package com.customeronboarding.customer.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CustomerResponseDTO {
    private Long customerId;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String address;
    private String panNumber;
    private String aadhaarNumber;

    private LocalDateTime createdAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String isActive;

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
}
