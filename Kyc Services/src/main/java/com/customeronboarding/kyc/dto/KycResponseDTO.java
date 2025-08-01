package com.customeronboarding.kyc.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycResponseDTO {
    private Long id;
    private Long customerId;
    private String aadhaarImagePath;
    private String panImagePath;
    private String photoImagePath;
    private String status;
    private String adminMessage;
    private String verifiedBy;
}
