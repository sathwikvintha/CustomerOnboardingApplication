package com.customeronboarding.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KycStatusResponseDTO {
    private Long id;
    private Long customerId;
    private String status;
    private String adminMessage;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
}
