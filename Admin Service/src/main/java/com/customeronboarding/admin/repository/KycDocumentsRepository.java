package com.customeronboarding.admin.repository;

import com.customeronboarding.admin.entity.KycDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycDocumentsRepository extends JpaRepository<KycDocuments, Long> {
    Optional<KycDocuments> findByCustomerCustomerId(Long customerId);
}
