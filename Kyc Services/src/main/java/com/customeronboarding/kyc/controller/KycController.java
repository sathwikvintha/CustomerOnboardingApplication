package com.customeronboarding.kyc.controller;

import com.customeronboarding.kyc.dto.*;
import com.customeronboarding.kyc.entity.KycStatusEnum;
import com.customeronboarding.kyc.feign.CustomerActivityClient;
import com.customeronboarding.kyc.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;
    private final CustomerActivityClient customerActivityClient;

    @PostMapping("/upload")
    public ResponseEntity<KycResponseDTO> uploadKyc(@RequestBody KycUploadRequestDTO dto) {
        KycStatusUpdateDTO currentStatus = kycService.getKycStatus(dto.getCustomerId());
        if (currentStatus != null && KycStatusEnum.REVERIFY_REQUIRED.name().equals(currentStatus.getStatus())) {
            kycService.resetKycForReupload(dto.getCustomerId());
        }
        KycResponseDTO response = kycService.uploadKycDocuments(dto);
        customerActivityClient.logActivity(dto.getCustomerId(), "KYC_UPLOAD", "KYC documents uploaded");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<KycResponseDTO> getKyc(@PathVariable Long customerId) {
        KycResponseDTO response = kycService.getKycByCustomerId(customerId);
        customerActivityClient.logActivity(customerId, "KYC_VIEWED", "KYC details viewed");
        return ResponseEntity.ok(kycService.getKycByCustomerId(customerId));
    }

    @GetMapping
    public ResponseEntity<List<KycResponseDTO>> getAllKycs() {
        return ResponseEntity.ok(kycService.getAllKycs());
    }

    @DeleteMapping("/{kycId}")
    public ResponseEntity<Void> deleteKyc(@PathVariable Long kycId) {
        Long customerId = kycService.getCustomerIdByKycId(kycId); // You may need to implement this
        kycService.deleteKyc(kycId);
        customerActivityClient.logActivity(customerId, "KYC_DELETED", "KYC entry deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{customerId}/status")
    public ResponseEntity<KycResponseDTO> updateKycStatus(@PathVariable Long customerId,
                                                          @RequestBody KycStatusUpdateDTO dto) {
        KycResponseDTO response = kycService.updateKycStatus(customerId, dto);
        customerActivityClient.logActivity(customerId, "KYC_STATUS_UPDATED", "KYC status updated to: " + dto.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/status")
    public ResponseEntity<KycStatusUpdateDTO> getKycStatus(@PathVariable Long customerId) {
        KycStatusUpdateDTO status = kycService.getKycStatus(customerId);
        customerActivityClient.logActivity(customerId, "KYC_STATUS_VIEWED", "KYC status viewed");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{customerId}/download")
    public ResponseEntity<byte[]> downloadKycDocuments(@PathVariable Long customerId) throws IOException {
        return kycService.downloadKycDocuments(customerId);
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<List<KycHistoryDTO>> getKycHistory(@PathVariable Long customerId) {
        List<KycHistoryDTO> history = kycService.getKycHistory(customerId);
        customerActivityClient.logActivity(customerId, "KYC_HISTORY_VIEWED", "Viewed KYC submission history");
        return ResponseEntity.ok(history);
    }

    @PostMapping("/resubmit")
    public ResponseEntity<String> resubmitKyc(@RequestBody ResubmitKycRequestDTO request) {
        kycService.resubmitKyc(request);
        customerActivityClient.logActivity(request.getCustomerId(), "KYC_RESUBMITTED", "KYC resubmitted after rejection");
        return ResponseEntity.ok("KYC resubmitted successfully.");
    }

    @GetMapping("/{customerId}/notifications")
    public ResponseEntity<List<KycNotificationDTO>> getKycNotifications(@PathVariable Long customerId) {
        List<KycNotificationDTO> notifications = kycService.getKycNotifications(customerId);
        customerActivityClient.logActivity(customerId, "KYC_NOTIFICATIONS_VIEWED", "Viewed KYC status notifications");
        return ResponseEntity.ok(notifications);
    }


}
