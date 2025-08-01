package com.customeronboarding.kyc.service;

import com.customeronboarding.kyc.dto.*;
import com.customeronboarding.kyc.entity.KycDocument;
import com.customeronboarding.kyc.entity.KycStatus;
import com.customeronboarding.kyc.enums.KycStatusEnum;
import com.customeronboarding.kyc.repository.KycDocumentRepository;
import com.customeronboarding.kyc.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final KycDocumentRepository documentRepo;
    private final KycStatusRepository statusRepo;
    private final KycStatusRepository kycStatusRepository;


    @Override
    public KycResponseDTO uploadKycDocuments(KycUploadRequestDTO dto) {
        Optional<KycStatus> existingStatusOpt = statusRepo.findByCustomerId(dto.getCustomerId());

        if (existingStatusOpt.isPresent()) {
            KycStatus existingStatus = existingStatusOpt.get();

            if (existingStatus.getStatus() == KycStatus.Status.PENDING || existingStatus.getStatus() == KycStatus.Status.VERIFIED) {
                throw new RuntimeException("KYC already submitted and not rejected. Cannot upload again.");
            }

            // Status is REJECTED → allow new upload and update status to PENDING
            KycDocument newDoc = KycDocument.builder()
                    .customerId(dto.getCustomerId())
                    .aadhaarImagePath(dto.getAadhaarImagePath())
                    .panImagePath(dto.getPanImagePath())
                    .photoImagePath(dto.getPhotoImagePath())
                    .uploadedAt(Timestamp.from(Instant.now()))
                    .build();
            documentRepo.save(newDoc);

            existingStatus.setStatus(KycStatus.Status.PENDING);
            existingStatus.setAdminMessage(null);
            existingStatus.setVerifiedBy(null);
            existingStatus.setVerifiedAt(null);
            statusRepo.save(existingStatus);

            return buildResponse(newDoc, existingStatus);

        } else {
            // First time upload
            KycDocument document = KycDocument.builder()
                    .customerId(dto.getCustomerId())
                    .aadhaarImagePath(dto.getAadhaarImagePath())
                    .panImagePath(dto.getPanImagePath())
                    .photoImagePath(dto.getPhotoImagePath())
                    .uploadedAt(Timestamp.from(Instant.now()))
                    .build();
            documentRepo.save(document);

            KycStatus status = KycStatus.builder()
                    .customerId(dto.getCustomerId())
                    .status(KycStatus.Status.PENDING)
                    .build();
            statusRepo.save(status);

            return buildResponse(document, status);
        }
    }

    @Override
    public KycResponseDTO getKycByCustomerId(Long customerId) {
        KycDocument doc = documentRepo.findTopByCustomerIdOrderByUploadedAtDesc(customerId)
                .orElseThrow(() -> new RuntimeException("No KYC document found"));
        KycStatus status = statusRepo.findByCustomerId(customerId).orElseThrow();
        return buildResponse(doc, status);
    }

    @Override
    public List<KycResponseDTO> getAllKycs() {
        List<KycStatus> statuses = statusRepo.findAll();
        return statuses.stream().map(status -> {
            Optional<KycDocument> docOpt = documentRepo.findTopByCustomerIdOrderByUploadedAtDesc(status.getCustomerId());
            return docOpt.map(doc -> buildResponse(doc, status)).orElse(null);
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    @Override
    public void deleteKyc(Long docId) {
        documentRepo.deleteById(docId);
    }

    @Override
    public KycResponseDTO updateKycStatus(Long customerId, KycStatusUpdateDTO dto) {
        KycStatus status = statusRepo.findByCustomerId(customerId).orElseThrow();
        status.setStatus(dto.getStatus());
        status.setAdminMessage(dto.getAdminMessage());
        status.setVerifiedBy(dto.getVerifiedBy() != null ? Long.parseLong(dto.getVerifiedBy()) : null);
        status.setVerifiedAt(Timestamp.from(Instant.now()));
        statusRepo.save(status);

        KycDocument document = documentRepo.findTopByCustomerIdOrderByUploadedAtDesc(customerId)
                .orElseThrow(() -> new RuntimeException("No KYC document found"));
        return buildResponse(document, status);
    }

    @Override
    public KycStatusUpdateDTO getKycStatus(Long customerId) {
        KycStatus status = statusRepo.findByCustomerId(customerId).orElseThrow();
        return KycStatusUpdateDTO.builder()
                .status(status.getStatus())
                .adminMessage(status.getAdminMessage())
                .verifiedBy(status.getVerifiedBy() != null ? status.getVerifiedBy().toString() : null)
                .build();
    }

    private KycResponseDTO buildResponse(KycDocument doc, KycStatus status) {
        return KycResponseDTO.builder()
                .id(doc.getDocId())
                .customerId(doc.getCustomerId())
                .aadhaarImagePath(doc.getAadhaarImagePath())
                .panImagePath(doc.getPanImagePath())
                .photoImagePath(doc.getPhotoImagePath())
                .status(status.getStatus().name())
                .adminMessage(status.getAdminMessage())
                .verifiedBy(status.getVerifiedBy() != null ? status.getVerifiedBy().toString() : null)
                .build();
    }

    @Override
    public void resetKycForReupload(Long customerId) {
        Optional<KycStatus> optional = kycStatusRepository.findByCustomerId(customerId);
        if (optional.isPresent()) {
            KycStatus kycStatus = optional.get();
            kycStatus.setStatus(KycStatus.Status.PENDING);  // FIXED
            kycStatus.setAdminMessage("Customer re-uploaded documents");
            kycStatus.setVerifiedBy(null);
            kycStatus.setVerifiedAt(null);
            kycStatusRepository.save(kycStatus);
        } else {
            throw new RuntimeException("KYC status not found for customerId: " + customerId);
        }
    }
}
