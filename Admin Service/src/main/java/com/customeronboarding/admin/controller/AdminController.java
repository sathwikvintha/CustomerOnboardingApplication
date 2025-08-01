package com.customeronboarding.admin.controller;

import com.customeronboarding.admin.dto.DashboardMetricsDTO;
import com.customeronboarding.admin.dto.KycReverifyRequestDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.entity.KycDocuments;
import com.customeronboarding.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/kyc-status")
    public ResponseEntity<String> updateKycStatus(@RequestBody KycStatusResponseDTO statusRequest) {
        return ResponseEntity.ok(adminService.updateKycStatus(statusRequest));
    }

    @GetMapping("/kyc-status/{customerId}")
    public ResponseEntity<KycStatusResponseDTO> getKycStatus(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.getKycStatus(customerId));
    }

    @GetMapping("/kyc-status/pending")
    public ResponseEntity<List<KycStatusResponseDTO>> getAllPendingKyc() {
        return ResponseEntity.ok(adminService.getAllKycByStatus("PENDING"));
    }

    @GetMapping("/kyc-status/verified")
    public ResponseEntity<List<KycStatusResponseDTO>> getAllVerifiedKyc() {
        return ResponseEntity.ok(adminService.getAllKycByStatus("VERIFIED"));
    }

    @GetMapping("/kyc-status/rejected")
    public ResponseEntity<List<KycStatusResponseDTO>> getAllRejectedKyc() {
        return ResponseEntity.ok(adminService.getAllKycByStatus("REJECTED"));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.deleteCustomer(customerId));
    }

    @GetMapping("/kyc-documents/{customerId}")
    public ResponseEntity<KycDocuments> getKycDocuments(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.getKycDocuments(customerId));
    }

    @GetMapping("/customers/no-kyc")
    public ResponseEntity<List<Customer>> getCustomersWithNoKyc() {
        return ResponseEntity.ok(adminService.getCustomersWithoutKyc());
    }

    @PutMapping("/kyc/mark-reverify")
    public ResponseEntity<String> markKycForReverification(@RequestBody KycReverifyRequestDTO request) {
        return ResponseEntity.ok(adminService.markKycReverifyRequired(request));
    }

    @GetMapping("/kyc-status")
    public ResponseEntity<Page<KycStatusResponseDTO>> getKycStatusByStatusPaged(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getKycStatusByStatusPaged(status, page, size));
    }

    @GetMapping("/customers/search-by-name")
    public ResponseEntity<List<Customer>> searchCustomersByName(@RequestParam String name) {
        return ResponseEntity.ok(adminService.searchCustomersByName(name));
    }

    @GetMapping("/customers/search-by-email")
    public ResponseEntity<List<Customer>> searchCustomersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(adminService.searchCustomersByEmail(email));
    }

    @GetMapping("/dashboard-metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        return ResponseEntity.ok(adminService.getDashboardMetrics());
    }


}
