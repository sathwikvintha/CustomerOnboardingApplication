package com.customeronboarding.admin.controller;

import com.customeronboarding.admin.dto.DashboardMetricsDTO;
import com.customeronboarding.admin.dto.KycReverifyRequestDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.dto.UserRegistrationRequestDTO;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.entity.KycDocuments;
import com.customeronboarding.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ✅ Update KYC status
    @PutMapping("/kyc-status")
    public ResponseEntity<String> updateKycStatus(@RequestBody KycStatusResponseDTO statusRequest) {
        return ResponseEntity.ok(adminService.updateKycStatus(statusRequest));
    }

    // ✅ Get single KYC status by customer ID
    @GetMapping("/kyc-status/{customerId}")
    public ResponseEntity<KycStatusResponseDTO> getKycStatus(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.getKycStatus(customerId));
    }

    // ✅ Get KYC records by status (VERIFIED / REJECTED / PENDING)
    @GetMapping("/kyc-status")
    public ResponseEntity<List<KycStatusResponseDTO>> getAllKycByStatus(@RequestParam String status) {
        return ResponseEntity.ok(adminService.getAllKycByStatus(status));
    }

    // ✅ Get all customers
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    // ✅ Delete customer
    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.deleteCustomer(customerId));
    }

    // ✅ Get KYC documents for a customer
    @GetMapping("/kyc-documents/{customerId}")
    public ResponseEntity<KycDocuments> getKycDocuments(@PathVariable Long customerId) {
        return ResponseEntity.ok(adminService.getKycDocuments(customerId));
    }

    // ✅ Get customers with no KYC submitted
    @GetMapping("/customers/no-kyc")
    public ResponseEntity<List<Customer>> getCustomersWithNoKyc() {
        return ResponseEntity.ok(adminService.getCustomersWithoutKyc());
    }

    // ✅ Mark KYC for re-verification
    @PutMapping("/kyc/mark-reverify")
    public ResponseEntity<String> markKycForReverification(@RequestBody KycReverifyRequestDTO request) {
        return ResponseEntity.ok(adminService.markKycReverifyRequired(request));
    }

    // ✅ Search customers by name
    @GetMapping("/customers/search-by-name")
    public ResponseEntity<List<Customer>> searchCustomersByName(@RequestParam String name) {
        return ResponseEntity.ok(adminService.searchCustomersByName(name));
    }

    // ✅ Search customers by email
    @GetMapping("/customers/search-by-email")
    public ResponseEntity<List<Customer>> searchCustomersByEmail(@RequestParam String email) {
        return ResponseEntity.ok(adminService.searchCustomersByEmail(email));
    }

    // ✅ Dashboard metrics for Admin Panel
    @GetMapping("/dashboard-metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        return ResponseEntity.ok(adminService.getDashboardMetrics());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody UserRegistrationRequestDTO request) {
        String message = adminService.registerCustomer(request);
        return ResponseEntity.ok(message);
    }

}
