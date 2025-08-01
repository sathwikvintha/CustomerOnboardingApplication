package com.customeronboarding.kyc.controller;

import com.customeronboarding.kyc.dto.*;
import com.customeronboarding.kyc.entity.KycStatusEnum;
import com.customeronboarding.kyc.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/upload")
    public ResponseEntity<KycResponseDTO> uploadKyc(@RequestBody KycUploadRequestDTO dto) {
        // Check if customer has REVERIFY_REQUIRED status
        KycStatusUpdateDTO currentStatus = kycService.getKycStatus(dto.getCustomerId());
        if (currentStatus != null && KycStatusEnum.REVERIFY_REQUIRED.name().equals(currentStatus.getStatus())) {
            // Reset KYC status to PENDING before processing fresh upload
            kycService.resetKycForReupload(dto.getCustomerId());
        }
        return ResponseEntity.ok(kycService.uploadKycDocuments(dto));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<KycResponseDTO> getKyc(@PathVariable Long customerId) {
        return ResponseEntity.ok(kycService.getKycByCustomerId(customerId));
    }

    @GetMapping
    public ResponseEntity<List<KycResponseDTO>> getAllKycs() {
        return ResponseEntity.ok(kycService.getAllKycs());
    }

    @DeleteMapping("/{kycId}")
    public ResponseEntity<Void> deleteKyc(@PathVariable Long kycId) {
        kycService.deleteKyc(kycId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{customerId}/status")
    public ResponseEntity<KycResponseDTO> updateKycStatus(@PathVariable Long customerId,
                                                          @RequestBody KycStatusUpdateDTO dto) {
        return ResponseEntity.ok(kycService.updateKycStatus(customerId, dto));
    }

    @GetMapping("/{customerId}/status")
    public ResponseEntity<KycStatusUpdateDTO> getKycStatus(@PathVariable Long customerId) {
        return ResponseEntity.ok(kycService.getKycStatus(customerId));
    }
}
