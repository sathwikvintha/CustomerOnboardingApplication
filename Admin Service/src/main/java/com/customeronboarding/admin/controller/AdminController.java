package com.customeronboarding.admin.controller;

import com.customeronboarding.admin.dto.DashboardMetricsDTO;
import com.customeronboarding.admin.dto.KycReverifyRequestDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.dto.UserRegistrationRequestDTO;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.entity.KycDocuments;
import com.customeronboarding.admin.feign.CustomerActivityClient;
import com.customeronboarding.admin.service.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CustomerActivityClient customerActivityClient;


    // ✅ Update KYC status
    @PutMapping("/kyc-status")
    public ResponseEntity<String> updateKycStatus(@RequestBody KycStatusResponseDTO statusRequest) {
        String result = adminService.updateKycStatus(statusRequest);
        customerActivityClient.logActivity(
                statusRequest.getCustomerId(),
                "UPDATE_KYC_STATUS",
                "Admin updated KYC status to " + statusRequest.getStatus()
        );
        return ResponseEntity.ok(result);
    }

    // ✅ Get single KYC status by customer ID
    @GetMapping("/kyc-status/{customerId}")
    public ResponseEntity<KycStatusResponseDTO> getKycStatus(@PathVariable Long customerId) {
        customerActivityClient.logActivity(
                customerId,
                "VIEW_KYC_STATUS",
                "Admin viewed KYC status"
        );
        return ResponseEntity.ok(adminService.getKycStatus(customerId));
    }

    // ✅ Get KYC records by status (VERIFIED / REJECTED / PENDING)
    @GetMapping("/kyc-status")
    public ResponseEntity<List<KycStatusResponseDTO>> getAllKycByStatus(@RequestParam String status) {
        customerActivityClient.logActivity(
                null,
                "VIEW_ALL_KYC_STATUS",
                "Admin fetched all KYC with status: " + status
        );
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
        String result = adminService.deleteCustomer(customerId);
        customerActivityClient.logActivity(
                customerId,
                "DELETE_CUSTOMER",
                "Admin deleted customer"
        );
        return ResponseEntity.ok(result);
    }

    // ✅ Get KYC documents for a customer
    @GetMapping("/kyc-documents/{customerId}")
    public ResponseEntity<KycDocuments> getKycDocuments(@PathVariable Long customerId) {
        customerActivityClient.logActivity(
                customerId,
                "VIEW_KYC_STATUS",
                "Admin viewed KYC status"
        );
        return ResponseEntity.ok(adminService.getKycDocuments(customerId));
    }

    // ✅ Get customers with no KYC submitted
    @GetMapping("/customers/no-kyc")
    public ResponseEntity<List<Customer>> getCustomersWithNoKyc() {
        customerActivityClient.logActivity(
                null,
                "VIEW_CUSTOMERS_WITHOUT_KYC",
                "Admin fetched customers who haven't submitted KYC"
        );
        return ResponseEntity.ok(adminService.getCustomersWithoutKyc());
    }

    // ✅ Mark KYC for re-verification
    @PutMapping("/kyc/mark-reverify")
    public ResponseEntity<String> markKycForReverification(@RequestBody KycReverifyRequestDTO request) {
        String result = adminService.markKycReverifyRequired(request);
        customerActivityClient.logActivity(
                request.getCustomerId(),
                "KYC_REVERIFY_REQUESTED",
                "Admin requested KYC re-verification"
        );
        return ResponseEntity.ok(result);
    }

    // ✅ Search customers by name
    @GetMapping("/customers/search-by-name")
    public ResponseEntity<List<Customer>> searchCustomersByName(@RequestParam String name) {
        customerActivityClient.logActivity(
                null,
                "SEARCH_CUSTOMERS_BY_NAME",
                "Admin searched customers by name: " + name
        );
        return ResponseEntity.ok(adminService.searchCustomersByName(name));
    }

    // ✅ Search customers by email
    @GetMapping("/customers/search-by-email")
    public ResponseEntity<List<Customer>> searchCustomersByEmail(@RequestParam String email) {
        customerActivityClient.logActivity(
                null,
                "SEARCH_CUSTOMERS_BY_EMAIL",
                "Admin searched customers by email: " + email
        );
        return ResponseEntity.ok(adminService.searchCustomersByEmail(email));
    }

    // ✅ Dashboard metrics for Admin Panel
    @GetMapping("/dashboard-metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        customerActivityClient.logActivity(
                null,
                "VIEW_DASHBOARD_METRICS",
                "Admin viewed dashboard metrics from " + from + " to " + to
        );
        return ResponseEntity.ok(adminService.getDashboardMetrics(from, to));
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody UserRegistrationRequestDTO request) {
        String message = adminService.registerCustomer(request);
        customerActivityClient.logActivity(
                null,
                "REGISTER_CUSTOMER",
                "Admin registered customer with username: " + request.getUsername()
        );
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/customers/{id}/reactivate")
    public ResponseEntity<String> reactivateCustomer(@PathVariable Long id) {
        adminService.reactivateCustomer(id);
        customerActivityClient.logActivity(
                id,
                "REACTIVATE_CUSTOMER",
                "Admin reactivated customer account"
        );
        return ResponseEntity.ok("Customer account reactivated successfully");
    }

    @GetMapping("/kyc/export")
    public void exportKycStatusToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=kyc_status_report.csv";
        response.setHeader(headerKey, headerValue);

        customerActivityClient.logActivity(
                null,
                "EXPORT_KYC_CSV",
                "Admin exported KYC status report to CSV"
        );
        adminService.exportKycStatusToCsv(response.getWriter());
    }


}
