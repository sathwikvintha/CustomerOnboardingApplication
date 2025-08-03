package com.customeronboarding.kyc.dto;

import lombok.Data;

@Data
public class ResubmitKycRequestDTO {
    private Long customerId;
    private String aadhaarImagePath;
    private String panImagePath;
    private String photoImagePath;
}
