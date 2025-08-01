package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.KycStatusUpdateRequest;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;

import java.util.List;

public interface KycStatusService {
    KycStatusResponseDTO updateKycStatus(KycStatusUpdateRequest request);
    KycStatusResponseDTO getKycStatus(String customerId);
    List<KycStatusResponseDTO> getAllPendingKyc();
}
