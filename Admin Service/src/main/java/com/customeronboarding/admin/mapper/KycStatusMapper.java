package com.customeronboarding.admin.mapper;

import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.entity.KycStatus;
import org.springframework.stereotype.Component;

@Component
public class KycStatusMapper {

    public KycStatusResponseDTO toKycStatusResponse(KycStatus status) {
        return KycStatusResponseDTO.builder()
                .id(status.getId())
                .customerId(Long.valueOf(status.getCustomer().getCustomerId().toString()))
                .status(String.valueOf(status.getStatus()))
                .adminMessage(status.getAdminMessage())
                .verifiedBy(status.getVerifiedBy())
                .verifiedAt(status.getVerifiedAt())
                .build();
    }
}
