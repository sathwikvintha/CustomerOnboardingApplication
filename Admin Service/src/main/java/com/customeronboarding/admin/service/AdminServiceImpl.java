package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.DashboardMetricsDTO;
import com.customeronboarding.admin.dto.KycReverifyRequestDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.entity.KycDocuments;
import com.customeronboarding.admin.entity.KycStatus;
import com.customeronboarding.admin.entity.KycStatusEnum;
import com.customeronboarding.admin.mapper.KycStatusMapper;
import com.customeronboarding.admin.repository.CustomerRepository;
import com.customeronboarding.admin.repository.KycDocumentsRepository;
import com.customeronboarding.admin.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
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
    public DashboardMetricsDTO getDashboardMetrics() {
        long totalCustomers = customerRepository.count();
        long verified = kycStatusRepository.countByStatus(KycStatusEnum.valueOf(String.valueOf(KycStatusEnum.VERIFIED)));
        long pending = kycStatusRepository.countByStatus(KycStatusEnum.valueOf(String.valueOf(KycStatusEnum.PENDING)));
        long rejected = kycStatusRepository.countByStatus(KycStatusEnum.valueOf(String.valueOf(KycStatusEnum.REJECTED)));
        long noKyc = customerRepository.countByKycStatusIsNull();

        return DashboardMetricsDTO.builder()
                .totalCustomers(totalCustomers)
                .kycVerified(verified)
                .kycPending(pending)
                .kycRejected(rejected)
                .kycNotSubmitted(noKyc)
                .build();
    }



}
