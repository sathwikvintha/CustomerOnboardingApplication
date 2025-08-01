package com.customeronboarding.admin.dto;

import lombok.Data;

@Data
public class KycReverifyRequestDTO {
    private Long customerId;
    private String adminMessage;  // Reason for reverification
    private String verifiedBy;
}
