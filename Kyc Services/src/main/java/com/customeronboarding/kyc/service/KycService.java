package com.customeronboarding.kyc.service;

import com.customeronboarding.kyc.dto.*;

import java.util.List;

public interface KycService {
    KycResponseDTO uploadKycDocuments(KycUploadRequestDTO dto);
    KycResponseDTO getKycByCustomerId(Long customerId);
    List<KycResponseDTO> getAllKycs();
    void deleteKyc(Long kycId);
    KycResponseDTO updateKycStatus(Long customerId, KycStatusUpdateDTO dto);
    KycStatusUpdateDTO getKycStatus(Long customerId);
    void resetKycForReupload(Long customerId);
}
