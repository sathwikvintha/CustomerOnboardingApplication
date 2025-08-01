package com.customeronboarding.admin.client;

import com.customeronboarding.admin.dto.KycDocumentsResponseDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "kyc-service", url = "http://localhost:8082/api/kyc")
public interface KycFeignClient {

    @GetMapping("/status/customer/{customerId}")
    KycStatusResponseDTO getKycStatus(@PathVariable Long customerId);

    @PutMapping("/status")
    String updateKycStatus(@RequestBody KycStatusResponseDTO dto);

    @GetMapping("/status/pending")
    List<KycStatusResponseDTO> getAllPendingKyc();

    @GetMapping("/documents/{customerId}")
    KycDocumentsResponseDTO getKycDocuments(@PathVariable Long customerId);
}
