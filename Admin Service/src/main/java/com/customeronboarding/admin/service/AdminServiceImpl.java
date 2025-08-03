package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.DashboardMetricsDTO;
import com.customeronboarding.admin.dto.KycReverifyRequestDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.dto.UserRegistrationRequestDTO;
import com.customeronboarding.admin.entity.*;
import com.customeronboarding.admin.feign.CustomerActivityClient;
import com.customeronboarding.admin.mapper.KycStatusMapper;
import com.customeronboarding.admin.repository.AdminUserRepository;
import com.customeronboarding.admin.repository.CustomerRepository;
import com.customeronboarding.admin.repository.KycDocumentsRepository;
import com.customeronboarding.admin.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final KycStatusRepository kycStatusRepository;
    private final CustomerRepository customerRepository;
    private final KycDocumentsRepository kycDocumentsRepository;
    private final KycStatusMapper mapper;
    private final CustomerActivityClient activityClient;
    @Autowired
    private ActivityService activityService;


    @Autowired
    private AdminUserRepository adminUserRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String updateKycStatus(KycStatusResponseDTO request) {
        Optional<KycStatus> optionalStatus = kycStatusRepository.findByCustomerCustomerId(request.getCustomerId());

        if (optionalStatus.isEmpty()) {
            throw new RuntimeException("KYC Status not found for Customer ID: " + request.getCustomerId());
        }

        KycStatus status = optionalStatus.get();
        status.setStatus(KycStatusEnum.valueOf(request.getStatus()));
        status.setAdminMessage(request.getAdminMessage());
        status.setVerifiedBy(request.getVerifiedBy());
        status.setVerifiedAt(LocalDateTime.now());

        kycStatusRepository.save(status);

        return "KYC Status updated successfully.";
    }

    @Override
    public KycStatusResponseDTO getKycStatus(Long customerId) {
        KycStatus status = kycStatusRepository.findByCustomerCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("KYC Status not found"));

        return mapper.toKycStatusResponse(status);
    }

    @Override
    public List<KycStatusResponseDTO> getAllKycByStatus(String status) {
        KycStatusEnum enumStatus;
        try {
            enumStatus = KycStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        return kycStatusRepository.findByStatus(enumStatus).stream()
                .map(mapper::toKycStatusResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public String deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
        return "Customer deleted successfully.";
    }

    @Override
    public KycDocuments getKycDocuments(Long customerId) {
        return kycDocumentsRepository.findByCustomerCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("KYC documents not found"));
    }

    @Override
    public List<Customer> getCustomersWithoutKyc() {
        List<Long> customersWithKyc = kycDocumentsRepository.findAll().stream()
                .map(doc -> doc.getCustomer().getCustomerId())
                .toList();

        return customerRepository.findAll().stream()
                .filter(c -> !customersWithKyc.contains(c.getCustomerId()))
                .toList();
    }

    @Override
    public String markKycReverifyRequired(KycReverifyRequestDTO request) {
        Optional<KycStatus> optionalStatus = kycStatusRepository.findByCustomerCustomerId(request.getCustomerId());

        if (optionalStatus.isEmpty()) {
            throw new RuntimeException("KYC Status not found for Customer ID: " + request.getCustomerId());
        }

        KycStatus status = optionalStatus.get();
        status.setStatus(KycStatusEnum.REVERIFY_REQUIRED);  // use new enum
        status.setAdminMessage(request.getAdminMessage());
        status.setVerifiedBy(request.getVerifiedBy());
        status.setVerifiedAt(LocalDateTime.now());

        kycStatusRepository.save(status);

        return "KYC marked for re-verification.";
    }

    @Override
    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    public List<Customer> searchCustomersByEmail(String email) {
        return customerRepository.findByEmailContainingIgnoreCase(email);
    }

    @Override
    public DashboardMetricsDTO getDashboardMetrics(LocalDate from, LocalDate to) {
        LocalDateTime start = from != null ? from.atStartOfDay() : LocalDate.MIN.atStartOfDay();
        LocalDateTime end = to != null ? to.atTime(LocalTime.MAX) : LocalDate.MAX.atTime(LocalTime.MAX);

        long totalCustomers = customerRepository.countByCreatedAtBetween(start, end);
        long verified = kycStatusRepository.countByStatusAndVerifiedAtBetween(KycStatusEnum.VERIFIED, start, end);
        long pending = kycStatusRepository.countByStatusAndCreatedAtBetween(KycStatusEnum.PENDING, start, end);
        long rejected = kycStatusRepository.countByStatusAndCreatedAtBetween(KycStatusEnum.REJECTED, start, end);
        long noKyc = customerRepository.countByKycStatusIsNullAndCreatedAtBetween(start, end);

        return DashboardMetricsDTO.builder()
                .totalCustomers(totalCustomers)
                .kycVerified(verified)
                .kycPending(pending)
                .kycRejected(rejected)
                .kycNotSubmitted(noKyc)
                .build();
    }


    @Override
    public String registerCustomer(UserRegistrationRequestDTO request) {
        // 1. Check if a user already exists with the given username (email)
        if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + request.getUsername());
        }

        AdminUser user = new AdminUser();
        user.setUsername(request.getUsername()); // Now taking username directly
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole().toUpperCase()); // Ensure role is uppercase
        user.setCreatedAt(LocalDateTime.now());
        adminUserRepository.save(user); // persist and retrieve saved user with ID

        return "Customer registered successfully.";
    }


    @Override
    public void reactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (Boolean.TRUE.equals(customer.getIsActive())) {
            throw new RuntimeException("Customer account is already active");
        }

        customer.setIsActive(String.valueOf("ACTIVE"));
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setUpdatedBy("ADMIN"); // or fetch actual admin username/email if available
        customerRepository.save(customer);

        // Also update USERS table
        AdminUser user = adminUserRepository.findById(customer.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        adminUserRepository.save(user);

        activityService.logActivity(user.getId(), "ACCOUNT_REACTIVATED", "Admin reactivated the customer's account");
    }

    @Override
    public void exportKycStatusToCsv(PrintWriter writer) {
        List<KycStatus> statuses = kycStatusRepository.findAll();

        writer.println("Customer ID,Status,Verified By,Verified At,Message");

        for (KycStatus status : statuses) {
            Long customerId = status.getCustomer() != null ? status.getCustomer().getCustomerId() : null;
            writer.printf("%s,%s,%s,%s,%s%n",
                    customerId != null ? customerId.toString() : "",
                    status.getStatus(),
                    status.getVerifiedBy() != null ? status.getVerifiedBy() : "",
                    status.getVerifiedAt() != null ? status.getVerifiedAt().toString() : "",
                    status.getAdminMessage() != null ? status.getAdminMessage().replace(",", " ") : ""
            );
        }
    }



}
