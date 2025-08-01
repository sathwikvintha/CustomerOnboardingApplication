package com.customeronboarding.kyc.repository;

import com.customeronboarding.kyc.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    Optional<KycDocument> findByCustomerId(Long customerId);
    Optional<KycDocument> findTopByCustomerIdOrderByUploadedAtDesc(Long customerId);

}
