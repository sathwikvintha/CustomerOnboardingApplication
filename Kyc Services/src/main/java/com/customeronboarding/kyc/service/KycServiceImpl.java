package com.customeronboarding.kyc.service;

import com.customeronboarding.kyc.dto.*;
import com.customeronboarding.kyc.entity.KycDocument;
import com.customeronboarding.kyc.entity.KycStatus;
import com.customeronboarding.kyc.enums.KycStatusEnum;
import com.customeronboarding.kyc.repository.KycDocumentRepository;
import com.customeronboarding.kyc.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Override
    public Long getCustomerIdByKycId(Long kycId) {
        return documentRepo.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC document not found for ID: " + kycId))
                .getCustomerId();
    }

    @Override
    public ResponseEntity<byte[]> downloadKycDocuments(Long customerId) throws IOException {
        Optional<KycDocument> optionalDocs = documentRepo.findByCustomerId(customerId);
        if (optionalDocs.isEmpty()) {
            throw new RuntimeException("KYC documents not found for customer ID: " + customerId);
        }

        KycDocument document = optionalDocs.get();

        // Load files from disk
        File aadhaarFile = new File(document.getAadhaarImagePath());
        File panFile = new File(document.getPanImagePath());
        File photoFile = new File(document.getPhotoImagePath());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        addToZip("aadhaar.png", aadhaarFile, zos);
        addToZip("pan.png", panFile, zos);
        addToZip("photo.png", photoFile, zos);

        zos.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("kyc_documents_" + customerId + ".zip").build());

        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

    private void addToZip(String fileName, File file, ZipOutputStream zos) throws IOException {
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
            fis.close();
        }
    }

    public List<KycHistoryDTO> getKycHistory(Long customerId) {
        List<KycDocument> docs = documentRepo.findByCustomerIdOrderByUploadedAtDesc(customerId);
        return docs.stream().map(doc -> {
            KycHistoryDTO dto = new KycHistoryDTO();
            dto.setKycId(doc.getDocId());
            dto.setAadhaarPath(doc.getAadhaarImagePath());
            dto.setPanPath(doc.getPanImagePath());
            dto.setPhotoPath(doc.getPhotoImagePath());
            dto.setUploadedAt(doc.getUploadedAt().toLocalDateTime());
            return dto;
        }).toList();
    }

    @Override
    public void resubmitKyc(ResubmitKycRequestDTO request) {
        // Validate if rejected
        KycStatus status = kycStatusRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("KYC status not found"));

        if (!"REJECTED".equalsIgnoreCase(String.valueOf(status.getStatus()))) {
            throw new RuntimeException("Only rejected KYC can be resubmitted.");
        }

        // Create new KYC Document record
        KycDocument doc = new KycDocument();
        doc.setCustomerId(request.getCustomerId());
        doc.setAadhaarImagePath(request.getAadhaarImagePath());
        doc.setPanImagePath(request.getPanImagePath());
        doc.setPhotoImagePath(request.getPhotoImagePath());
        doc.setUploadedAt(Timestamp.valueOf(LocalDateTime.now()));
        documentRepo.save(doc);

        // Update KYC status to PENDING
        status.setStatus(KycStatus.Status.valueOf("PENDING"));
        status.setVerifiedBy(null);
        status.setVerifiedAt(null);
        status.setAdminMessage("Resubmitted by customer");
        kycStatusRepository.save(status);
    }

    public List<KycNotificationDTO> getKycNotifications(Long customerId) {
        List<KycStatus> statusList = kycStatusRepository.findAllByCustomerIdOrderByVerifiedAtDesc(customerId);
        return statusList.stream().map(status -> {
            KycNotificationDTO dto = new KycNotificationDTO();
            dto.setStatus(String.valueOf(status.getStatus()));
            dto.setMessage(status.getAdminMessage());
            dto.setTimestamp(status.getVerifiedAt().toLocalDateTime());
            return dto;
        }).toList();
    }

}
