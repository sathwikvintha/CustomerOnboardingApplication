package com.customeronboarding.kyc.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycUploadRequestDTO {
    private Long customerId;
    private String aadhaarImagePath;
    private String panImagePath;
    private String photoImagePath;
}
