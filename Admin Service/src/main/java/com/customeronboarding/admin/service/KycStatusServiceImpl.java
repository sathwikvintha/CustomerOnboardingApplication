package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.KycStatusUpdateRequest;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.entity.KycStatus;
import com.customeronboarding.admin.entity.KycStatusEnum;
import com.customeronboarding.admin.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KycStatusServiceImpl implements KycStatusService {

    private final KycStatusRepository repository;

    @Override
    public KycStatusResponseDTO updateKycStatus(KycStatusUpdateRequest request) {
        KycStatus status = repository.findById(Long.valueOf(request.getCustomerId())).orElseThrow(() ->
                new RuntimeException("Customer ID not found"));

        status.setStatus(KycStatusEnum.valueOf(request.getStatus()));
        status.setAdminMessage(request.getAdminMessage());
        status.setVerifiedBy(request.getVerifiedBy());
        status.setVerifiedAt(LocalDateTime.now());

        repository.save(status);

        return mapToResponse(status);
    }

    @Override
    public KycStatusResponseDTO getKycStatus(String customerId) {
        return repository.findById(Long.valueOf(customerId))
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<KycStatusResponseDTO> getAllPendingKyc() {
        return repository.findByStatus(KycStatusEnum.valueOf("PENDING")).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private KycStatusResponseDTO mapToResponse(KycStatus status) {
        return KycStatusResponseDTO.builder()
                .customerId(status.getCustomer().getCustomerId())
                .status(String.valueOf(status.getStatus()))
                .adminMessage(status.getAdminMessage())
                .verifiedBy(status.getVerifiedBy())
                .verifiedAt(status.getVerifiedAt())
                .build();
    }
}
