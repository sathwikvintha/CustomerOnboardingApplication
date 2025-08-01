package com.customeronboarding.admin.dto;

import lombok.Data;

@Data
public class KycStatusUpdateRequest {
    private String customerId;
    private String status; // VERIFIED or REJECTED
    private String adminMessage;
    private String verifiedBy;
}
