package com.customeronboarding.kyc.dto;

public class KycStatusUpdateRequest {
    private String status;
    private String adminMessage; // optional
    private Long verifiedBy;     // optional
}
