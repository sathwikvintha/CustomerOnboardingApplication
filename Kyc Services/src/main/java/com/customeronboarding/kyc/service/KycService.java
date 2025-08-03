package com.customeronboarding.kyc.service;

import com.customeronboarding.kyc.dto.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface KycService {
    KycResponseDTO uploadKycDocuments(KycUploadRequestDTO dto);
    KycResponseDTO getKycByCustomerId(Long customerId);
    List<KycResponseDTO> getAllKycs();
    void deleteKyc(Long kycId);
    KycResponseDTO updateKycStatus(Long customerId, KycStatusUpdateDTO dto);
    KycStatusUpdateDTO getKycStatus(Long customerId);
    void resetKycForReupload(Long customerId);
    Long getCustomerIdByKycId(Long kycId);
    ResponseEntity<byte[]> downloadKycDocuments(Long customerId) throws IOException;
    List<KycHistoryDTO> getKycHistory(Long customerId);
    void resubmitKyc(ResubmitKycRequestDTO request);
    List<KycNotificationDTO> getKycNotifications(Long customerId);
}
