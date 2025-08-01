package com.customeronboarding.kyc.dto;

import com.customeronboarding.kyc.entity.KycStatus.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycStatusUpdateDTO {
    private Status status;
    private String adminMessage;
    private String verifiedBy;
}
